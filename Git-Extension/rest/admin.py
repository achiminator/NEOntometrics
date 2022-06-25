from django.contrib import admin
from .models import Repository, Commit, ClassMetrics, OntologyFile

admin.site.register(Repository)
admin.site.register(OntologyFile)

admin.site.register(Commit)

admin.site.register(ClassMetrics)

# Register your models here.
