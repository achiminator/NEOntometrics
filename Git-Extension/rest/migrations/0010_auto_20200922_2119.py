# Generated by Django 3.1.1 on 2020-09-22 19:19

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('rest', '0009_auto_20200922_2118'),
    ]

    operations = [
        migrations.AlterField(
            model_name='metrics',
            name='DLexpressivity',
            field=models.CharField(blank=True, default='', max_length=15),
        ),
        migrations.AlterField(
            model_name='metrics',
            name='Inversefunctionalobjectpropertiesaxiomscount',
            field=models.PositiveIntegerField(default=0),
        ),
    ]
