import logging
import logging
import django_rq
from django.db import DatabaseError
from django import urls
from rest.CalculationHelper import GitUrlParser, GitHelper
from rest.models import OntologyFile, Repository


class QueueInformation:

    taskFinished = None
    taskStarted = None
    queuePosition = None
    url = None
    error = False
    errorMessage = ""
    urlInSystem = False
    performsUpdate = False
    analyzedOntologies = None
    analysableOntologies = None
    totalCommits = None
    analyzedCommits = None

    def __init__(self, urlString: str) -> dict:
        self.url = GitUrlParser()
        self.url.parse(urlString)
        jobId = GitHelper.serializeJobId(self.url)
        queue = django_rq.get_queue()
        if jobId in queue.job_ids or jobId in queue.started_job_registry:
            redis_conn = django_rq.get_connection()
            job = django_rq.jobs.Job(jobId, redis_conn)
            jobPosition = django_rq.get_queue().get_job_position(job)
            self.taskFinished = False
            self.urlInSystem = True
            if Repository.objects.filter(repository=self.url.repository).exists():
                # If the flag "WholeRepositoryAnalyzed" is set AND the repository is in the queue, then it must perform an update. Thus we set flag.
                self.performsUpdate = Repository.objects.get(repository=self.url.repository).wholeRepositoryAnalyzed

            self.taskStarted = job in django_rq.get_queue().started_job_registry
            self.queuePosition = jobPosition if jobPosition != None else 0
            selfprogress = job.get_meta()
            if "analyzedOntologies" in selfprogress:
                self.analyzedOntologies = selfprogress["analyzedOntologies"]
            if "analysableOntologies" in selfprogress:
                self.analysableOntologies = selfprogress["analysableOntologies"]
            if "totalCommits" in selfprogress:
                self.totalCommits = selfprogress["totalCommits"]
            if "ananlyzedCommits" in selfprogress:
                self.analyzedCommits = selfprogress["ananlyzedCommits"]

        elif jobId in django_rq.get_queue().failed_job_registry:
            self.error = True
            self.urlInSystem = True
            job = django_rq.get_queue().fetch_job(jobId)
            if ("status code: 404" in job.exc_info) or ("KeyError: \"reference" in job.exc_info):
                self.errorMessage = "No valid Ontology found"
            else:
                self.errorMessage = "internalServerError"

        else:
            if(self.url.file == '' and self.url.repository != ''):
                query = Repository.objects.filter(repository=self.url.repository)
            elif(self.url.repository == '' and self.url.file != ''):
                query = OntologyFile.objects.filter(fileName=self.url.file)
            elif(self.url.repository != '' and self.url.file != ''):
                query = Repository.objects.filter(repository=self.url.repository)
                q2 = []
                for element in query:
                    q2.append(OntologyFile.objects.filter(repository = element, fileName = self.url.file))
                if(query.count() > 0):
                    query = q2
            else:
                throw: DatabaseError("No given Data in the Database")
            if(query.count() > 0):
                self.urlInSystem = True
                self.taskFinished = True

    def getQueueAnswer(self, url: GitUrlParser, jobId: str) -> dict:
        """Generates the response if the ontology to calculate is not yet finished

        Args:
            url (GitUrlParser): The GitUrlParser-Object containing information on the target repository/file
            jobId (str): The Job-ID/Request URL

        Returns:
            dict: Output Dict ready for JSON-Serialization
        """
        logging.debug("Job" + url.url + " is already in Queue")
        redis_conn = django_rq.get_connection()
        job = django_rq.jobs.Job(jobId, redis_conn)

        jobPosition = django_rq.get_queue().get_job_position(job)

        resp = {
            "taskFinished": False,
            "taskIsStarted": job in django_rq.get_queue().started_job_registry,
            "queuePosition": jobPosition if jobPosition != None else 0,
            "progress": job.get_meta()
        }
        resp.update(url.__dict__)
        return resp

    def getFailedQueueAnswer(self, jobId: str) -> dict:
        job = django_rq.get_queue().fetch_job(jobId)
        resp = {
            "status": 400,
            "url": GitHelper.deserializeJobId(jobId),
            "info": "No valid Ontology or Git-Repository found at this URL. Check your Query!"
        }
