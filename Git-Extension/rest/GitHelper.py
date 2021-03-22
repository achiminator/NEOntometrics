from urllib.parse import urlparse
import re

from pygit2 import repository

class GitHelper:
    """HelperClass for Various supporting functions
    """    
    @staticmethod
    def str2bool(v: str) -> bool:
        """Converts a String to a bool value

        Args:
            v (str): Input Value

        Raises:
            argparse.ArgumentTypeError: No Boolean Value detected

        Returns:
            bool: Boolean value
        """
        if isinstance(v, bool):
            return v
        if v.lower() in ('yes', 'true', 't', 'y', '1'):
            return True
        elif v.lower() in ('no', 'false', 'f', 'n', '0'):
            return False
        else:
            raise argparse.ArgumentTypeError('Boolean value expected.')

    def serializeJobId(input: str) -> str:
        """Replaces the **/** of a string with __II__, as a slash triggers a fail of the django_rq frontend

        Args:
            input (str): URL to ontology

        Returns:
            str: URL with escaped **/** symbol
        """
        return(input.replace("/", "__II__"))

    def deserializeJobId(input: str) -> str:
        """Reverses the serializeJob-Method

        Args:
            input (str): Job-ID

        Returns:
            str: URL containing the request
        """
        return(input.replace("__II__", "/"))
class GitUrlParser:
    """Parses and stores the information for the Git-URL.
    """
           
    repository =""
    url = ""
    file = ""
    service = ""
    branch =""
    def parse(self, input: str):
        """Parses a Git-URL and saves it into Repository (git-service, including owner and repo). If a specific file is assessed,
        the service stores the file-URL and the branch. If the is concerned with a specific file, but **not** in a git-repository,
        just the file is sstored.

        Args:
            input (str): URL to git-REPO or Git-File

        """
        self.url = input
        urlParsed = urlparse(input)
        # Check if an URL to a ontology file is given
        if ("rdf" or "owl" or "ttl") in input:
            # Check if the URL directly accesses an link
            if "blob" in input:
                self.repository = input.split("blob")[0]
                self.repository = self.repository[self.repository.index(urlParsed.netloc): -1]
                self.branch = input.split("blob")[1].split("/")[1]
                self.file = input.split("blob")[1][len(self.branch)+2:]
            else:
                self.file = self.url[self.url.index(urlParsed.netloc):]
        else:
            self.repository = self.url[self.url.index(urlParsed.netloc):]
        if self.repository.endswith("/"):
            self.repository = self.repository[:-1]
        self.service = urlParsed.netloc
