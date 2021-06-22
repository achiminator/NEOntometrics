import json
from math import pi

from bokeh.models.tools import HoverTool, BoxZoomTool, ResetTool, SaveTool
from bokeh.plotting import figure, output_file, show


def Jsonread(Json):
    """liest den JSON Code ein und gibt ein Dictionary zurück."""
    json_object = json.loads(Json)
    pairs =json_object
    return pairs

def get_X(pairs):
    """extrahiert aus dem Dictionary alle Schlüssel und speichert diese in eine Liste"""
    return list(pairs.keys())


def get_Y(pairs):
    """extrahiert aus dem Dictionary alle Values und speichert diese in eine Liste"""
    return list(pairs.values())

# die ersten Funktionen dienten nur der Unterstützung


def chooseGrafik(Json, Auswahl):
    """Auswahl der Grafiken und Plot dieser. Aufruf mit chooseGrafik(Json, Auswahl).
       Übergebe als Parameter entsprechenden JSON Code und der Art des Plottes"""
    if Auswahl == "line":
        line(Json)
    elif Auswahl == "bar":
        bar(Json)
    elif Auswahl == "circle":
        circle(Json)
    else:
         """weitere Figuren können hinzugefügt werden"""
         print("Figurtyp nicht vorhanden")

    #output_file("NEOntometrics.html")






def line(Json):
    pairs = Jsonread(Json)
    X = get_X(pairs)
    Y = get_Y(pairs)
    p = figure(title="verschiedene Figuren ", x_range=X, y_axis_label='y',
               toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()],

               tooltips=[("LOCATION", "@x"), ("TOTAL", "@y"), ])
    p.xaxis.major_label_orientation = pi / 4

    p.line(X, Y, legend_label="line", line_color="blue", line_width=2)  # Linienplot/Graph
    output_file("NEOntometrics.html")


    show(p)



def bar(Json):
    pairs = Jsonread(Json)
    X = get_X(pairs)
    Y = get_Y(pairs)
    p = figure(title="verschiedene Figuren ", x_range=X, y_axis_label='y',
               toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()],

               tooltips=[("LOCATION", "@x"), ("TOTAL", "@top"), ]

               )
    p.xaxis.major_label_orientation = pi / 4

    p.vbar(X, top=Y, legend_label="bar", width=0.5, bottom=0,
           color="blue")
    output_file("NEOntometrics.html")

    show(p)

def circle(Json):
    pairs = Jsonread(Json)
    X = get_X(pairs)
    Y = get_Y(pairs)
    p = figure(title="Figur ", x_range=X, y_axis_label='y',
               toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()], tooltips=[("LOCATION", "@x"), ("TOTAL", "@y"), ])

    p.xaxis.major_label_orientation = pi / 4
    p.circle(X, Y, legend_label="circle", fill_color="blue", size=12)  # Kreise/ Punkte
    output_file("NEOntometrics.html")
    show(p)
