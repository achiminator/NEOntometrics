# Generated by Django 3.2.1 on 2021-05-25 09:00

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('rest', '0019_metrics_size'),
    ]

    operations = [
        migrations.AlterField(
            model_name='metrics',
            name='ReadingError',
            field=models.TextField(default=False),
        ),
    ]
