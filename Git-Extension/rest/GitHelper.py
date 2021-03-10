from urllib.parse import urlparse
import re

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
        return(input.replace("/", "__II__"))

    def deserializeJobId(input: str) -> str:
        return(input.replace("__II__", "/"))
class GitUrlParser:
    """Parses and stores the information for the Git-URL.
    """
           
    repository =""
    url = ""
    file = ""
    branch =""
    def parse(self, input: str):
        """Parses a Git-URL and saves it into Repository (git-service, including owner and repo). If a specific file is assessed,
        the service stores the file-URL and the branch

        Args:
            input (str): URL to git-REPO or Git-File

        """
        self.url = input
        if("blob" in input):
            self.repository = input.split("blob")[0]
            self.branch = input.split("blob")[1].split("/")[1]
            self.file = input.split("blob")[1][len(self.branch)+1:]
        else:
            self.urlParsed = urlparse(input)
            self.repository = re.findall("^(\/\w*\/\w*)", urlParsed.path)[0]
        