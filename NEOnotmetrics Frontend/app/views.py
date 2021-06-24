from django.shortcuts import render




# Create your views here.

def index(request):
	return render(request,'app/index.html')

def RestResponse(request):
	return render(request,'app/RestResponse.html')

def header(request):
	return render(request,'app/header.html')

def footer(request):
	return render(request,'app/footer.html')

def Visualizations(request):
	return render(request, 'app/Visualizations.html')