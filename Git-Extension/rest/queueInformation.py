import logging, logging, django_rq
from django import urls
from rest.GitHelper import GitUrlParser, GitHelper
from rest.dbHandler import DBHandler


class QueueInformation:
        
    taskFinished = None
    taskStarted = None
    queuePosition = -1        
    url = None
    error = False
    errorMessage = ""
    urlInSystem = False
    
    def __init__(self, urlString: str) -> dict:
        self.url = GitUrlParser()
        self.url.parse(urlString)
        jobId = GitHelper.serializeJobId(self.url)
        if jobId in django_rq.get_queue().job_ids:
            redis_conn = django_rq.get_connection()
            job = django_rq.jobs.Job(jobId, redis_conn)
            jobPosition = django_rq.get_queue().get_job_position(job)
            self.taskFinished = False
            self.urlInSystem = True
            self.taskStarted  = job in django_rq.get_queue().started_job_registry
            self.queuePosition = jobPosition if jobPosition != None else 0
            selfprogress = job.get_meta()
            
        if jobId in django_rq.get_queue().failed_job_registry:
            self.error = True
            self.urlInSystem = True
            job = django_rq.get_queue().fetch_job(jobId)
            if ("status code: 404" in job.exc_info) or ("KeyError: \"reference" in job.exc_info):
                self.errorMessage = "No valid Ontology found"
            else:
                self.errorMessage = "internalServerError"

        else:
            db = DBHandler()
            metricFromDB = db.getMetricForOntology(
                file=self.url.file, repository=self.url.repository, hideId=False)
            if(metricFromDB):
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