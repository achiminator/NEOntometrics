import requests, threading, time


def getResults(url: str):
    print(url)
    time.sleep(5)
    print("{0} is over".format(url))

with open('repos.txt') as f:
    repos = f.readlines()
for repo in repos:
    # Creates a request-Thread for every ontology repo to put all ontologies at once in the queue and wait for the results
    threading.Thread(target=getResults, args=(repo,)).start()

# Show the active Threadcount (Programm is terminated as soon as just one thread (main thread) remains)
while(threading.active_count() > 1):
    print("{0} of in total {1} repositories are analyzed".format(threading.active_count() - 1, len(repos)), end="\r")
    time.sleep(0.2)

