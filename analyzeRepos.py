import requests, threading, time, json

from requests import status_codes

#metricsEndPoint = "http://fittony.gg01.local:8012/git?classMetrics=True&"
metricsEndPoint = "http://localhost:8006/git?classMetrics=False&reasoner=False&url="
error = 0
def getResults(url: str):
    requestURL = metricsEndPoint + url
    resp = requests.get(requestURL)
    #while (resp.status_code == 200 and "taskFinished" in json.loads(resp.content)):
    time.sleep(4)
    resp = requests.get(requestURL) 
    if(resp.status_code != 200):
        print(requestURL + "did not analyzed correctly")
        print(resp.content)
    print("\n{0} is over".format(url))

with open('repos.txt') as f:
    repos = f.readlines()
for repo in repos:
    # Creates a request-Thread for every ontology repo to put all ontologies at once in the queue and wait for the results
    if(repo.strip()[0] != '#'):
        threading.Thread(target=getResults, args=(repo.strip(),)).start()
# Show the active Threadcount (Programm is terminated as soon as just one thread (main thread) remains)
#while(threading.active_count() > 1):
 #   print("{0} of in total {1} repositories are analyzed. {2} were errornous".format(threading.active_count() - 1, len(repos), error), end="\r")
  #  time.sleep(1)

