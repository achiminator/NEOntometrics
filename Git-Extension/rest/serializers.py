from typing import OrderedDict
from rest_framework import serializers
from rest.models import OntologyFile, Commit, Repository


class CommitSerializer(serializers.ModelSerializer):
    class Meta:
        model = Commit
        fields = '__all__'


class CommitIDBranchSerializer(serializers.ModelSerializer):
    class Meta:
        model = Commit
        fields = ["CommitID", "branch"]
class CommitIDsInOntologySerializers(serializers.ModelSerializer):
    """Internally used to access the nested CommitIDs in the ontology by the DBCommitsInRepository Serializer

    """
    commit = CommitIDBranchSerializer(many= True)
    class Meta:
        model = OntologyFile
        fields=["commit"]
class DBCommitsInRepositorySerializer(serializers.ModelSerializer):
    """Entrypoint for the gathering of the already analyzedCommits for each repostiroy

    """
    ontologyfile_set = CommitIDsInOntologySerializers(many=True, read_only=True)
    class Meta:
        model = Repository
        fields = ["ontologyfile_set"]
    def flattenReponse(commitList:OrderedDict):
        """The results from the serializer are highly structured. However, we are just interested in a 
        general list of all previously analyzed commits. This method extracts the List of unique CommitIDs

        Args:
            commitList (OrderedDict): Input of DBCommitsInRepositorySerializer instance .data

        Returns:
            dict: CommitIDs for a file and the corresponding branches. The branches are 
            necessary for an additional check for change. The metrics are stable, however the brancehs can vary
            due to merges.
        """
        commitList  = commitList["ontologyfile_set"]
        existingCommitIDs = {}
        for element in commitList:
            for commit in element["commit"]:
                if commit["CommitID"] not in existingCommitIDs:
                    existingCommitIDs.update({commit["CommitID"]: eval(commit["branch"])})
        return existingCommitIDs
