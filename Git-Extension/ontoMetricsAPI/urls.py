"""ontoMetricsAPI URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from posixpath import basename
from django.contrib import admin
from rest_framework import routers
from django.urls import include, path
from rest import views
from django.conf import settings
from graphene_django.views import GraphQLView
from rest.schema import schema
from django.views.decorators.csrf import csrf_exempt

urlpatterns = [

    path('', views.index.as_view()),
    path('admin/', admin.site.urls),
    path('api', views.CalculateMetric.as_view()),
    path('git', views.CalculateGitMetric.as_view()),
    path('metricexplorer', views.MetricExplorer.as_view()),
    path('django-rq/', include('django_rq.urls')),
    path("graphiql", GraphQLView.as_view(graphiql=True, schema=schema)),
    path("graphql", csrf_exempt(GraphQLView.as_view(graphiql=False, schema=schema))),
]
