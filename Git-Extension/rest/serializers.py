from typing import OrderedDict
from rest_framework import serializers
from rest.models import OntologyFile, Commit, Repository


class CommitSerializer(serializers.ModelSerializer):
    class Meta:
        model = Commit
        fields = '__all__'

class CommitIDsInOntologySerializers(serializers.ModelSerializer):
    """Internally used to access the nested CommitIDs in the ontology by the DBCommitsInRepository Serializer

    """#
    commit = serializers.SlugRelatedField(many=True, read_only=True, slug_field="CommitID")
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
            list: Unique Commit IDs
        """
        commitList  = commitList["ontologyfile_set"]
        existingCommitIDs = []
        for element in commitList:
            for commitID in element["commit"]:
                if commitID not in existingCommitIDs:
                    existingCommitIDs.append(commitID)
        return existingCommitIDs
