from rest_framework import serializers
from rest.models import Metrics, Source

class MetricSerializer(serializers.ModelSerializer):
    class Meta:
        model = Source
        fields = '__all__'