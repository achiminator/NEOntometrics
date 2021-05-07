from django.urls import path
from . import views


urlpatterns = [
    path('', views.index, name="index"),
    path('Calculation', views.RestResponse, name="RestResponse"),
    path('head', views.header, name="header"),
    path('foot', views.footer, name="footer"),

]
