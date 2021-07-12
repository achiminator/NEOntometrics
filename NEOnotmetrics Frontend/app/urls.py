from django.urls import path
from . import views


urlpatterns = [
    path('', views.index, name="index"),
    path('Calculation', views.RestResponse, name="RestResponse"),
    path('Lineplot', views.Lineplot, name = 'Lineplot'),
    path('barplot', views.Barplot, name = 'Barplot'),
    path('Circleplot', views.Circleplot, name = 'Circleplot'),
    path('head', views.header, name="header"),
    path('foot', views.footer, name="footer"),

]
