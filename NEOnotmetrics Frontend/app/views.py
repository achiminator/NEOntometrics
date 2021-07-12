from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.
import json


from bokeh.plotting import figure
from bokeh.plotting import figure, output_file, show
from bokeh.models import ColumnDataSource, FactorRange, callbacks, CustomJS
from bokeh.models.tools import HoverTool, BoxZoomTool, PanTool, ResetTool, SaveTool
from math import pi
from bokeh.embed import components
from bokeh.models import ColorPicker
from bokeh.layouts import column





def Jsonread(Json): # 20 - 21 lassen sich vermutlich auch Zusammenfassen
    """liest den JSON Code ein und gibt ein Dictionary zurück."""
    json_object = json.loads(Json)
    pairs =json_object #json_object.items()
    return pairs

def get_X(pairs):
    """extrahiert aus dem Dictionary alle Schlüssel und speichert diese in eine Liste"""
    return list(pairs.keys())


def get_Y(pairs):
    """siehe get_X,  nur für die Values """
    return list(pairs.values())











def index(request):
	return render(request,'app/index.html')

def RestResponse(request):
	return render(request,'app/RestResponse.html')

def Lineplot(request):
	""" sollte wenn möglich einen Lineplot erzeugen. Hab versucht das mit Callbacks eizulesen"""

	callback= CustomJS(args=dict(),code="""var JsonObj = localStorage.getItem("VisuJSON");""")
	Json = callback.set_from_json()
	pairs = Jsonread(Json)
	X = get_X(pairs)
	Y = get_Y(pairs)
	p = figure(title="verschiedene Figuren ", x_range=X, y_axis_label='y',
			   toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()],

			   tooltips=[("LOCATION", "@x"), ("TOTAL", "@y"), ])
	p.xaxis.major_label_orientation = pi / 4

	p.line(X, Y, legend_label="line", line_color="blue", line_width=2)  # Linienplot/Graph
	script, div = components(p)
	return render(request, 'app/Lineplot.html', {'script': script, 'div': div})

def Barplot(request):
	callback = CustomJS(args=dict(), code="""var JsonObj = localStorage.getItem("VisuJSON");""")
	Json = callback.set_from_json()
	pairs = Jsonread(Json)
	X = get_X(pairs)
	Y = get_Y(pairs)
	p = figure(title="verschiedene Figuren ", x_range=X, y_axis_label='y',
			   toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()],

			   tooltips=[("LOCATION", "@x"), ("TOTAL", "@top"), ])
	p.xaxis.major_label_orientation = pi / 4

	p.vbar(X, top=Y, legend_label="bar", width=0.5, bottom=0,color="blue")

	script, div = components(p)
	return render(request, 'app/Barplot.html', {'script': script, 'div':div})

def Circleplot(request):
	callback = CustomJS(args=dict(), code="""var JsonObj = localStorage.getItem("VisuJSON");""")
	Json = callback.set_from_json()
	pairs = Jsonread(Json)
	X = get_X(pairs)
	Y = get_Y(pairs)
	p = figure(title="Figur ", x_range=X, y_axis_label='y',
			   toolbar_location="right", tools=[HoverTool(), BoxZoomTool(), ResetTool(), SaveTool()],
			   tooltips=[("LOCATION", "@x"), ("TOTAL", "@y"), ])

	p.xaxis.major_label_orientation = pi / 4
	p.circle(X, Y, legend_label="circle", fill_color="blue", size=12)  # Kreise/ Punkte

	script, div = components(p)

	return render(request, 'app/Barplot.html', {'script': script, 'div': div})

def header(request):
	return render(request,'app/header.html')

def footer(request):
	return render(request,'app/footer.html')