from pickletools import read_floatnl
from rest_framework import serializers
from rest.models import Metrics, Source

class _MetricDetailSerializer(serializers.ModelSerializer):
    class Meta:
        model=Metrics
        fields = '__all__'
        
class MetricSerializer(serializers.ModelSerializer):
    metrics = _MetricDetailSerializer(many=True, read_only=True)
    class Meta:
        model = Source
        # fields = "__all__"
        fields = ["created", "repository", "fileName", "wholeRepositoryAnalyzed", "metrics"]


