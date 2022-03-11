from django.contrib import admin
from .models import Source, Metrics, ClassMetrics

admin.site.register(Source)

admin.site.register(Metrics)

admin.site.register(ClassMetrics)

# Register your models here.
