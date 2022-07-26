import pymysql, os, logging.config
"""
Django settings for ontoMetricsAPI project.

Generated by 'django-admin startproject' using Django 3.0.7.

For more information on this file, see
https://docs.djangoproject.com/en/3.0/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/3.0/ref/settings/
"""

# There seems to be a Bug in the graphql-django package.
# This might fix it until fix-relase
import django
from django.utils.encoding import force_str
django.utils.encoding.force_text = force_str

import os

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


# Put the line provided below into your `urlpatterns` list.


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/3.0/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '+d9$bcwd(^4o2862x$-xe6kl0$sqcbtkkib+6@2zv*!ysnz)2o'


# SECURITY WARNING: don't run with debug turned on in production!
DEBUG =  False if (os.environ.get("inDocker", False)) else True

logging.config.dictConfig({
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'console': {
            # exact format is not important, this is the minimum information
            'format': '%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
        },
    },
    'handlers': {
        'console': {
            'class': 'logging.StreamHandler',
            'formatter': 'console',
        },
    },
    'loggers': {
    # root logger
        '': {
            'level': 'WARNING',
            'handlers': ['console'],
        },
    },
})

ALLOWED_HOSTS = ["*", "localhost"]

# REST_FRAMEWORK = {
#     'DEFAULT_RENDERER_CLASSES': [
#         'rest_framework.renderers.JSONRenderer',
#     ]
# }

# Application definition

INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django.contrib.sites',
    'sorl.thumbnail',
    'rest_framework',
    'rest',
    'django_filters',
    'django_rq',
    'corsheaders',
    'graphene_django',
    'newsletter'
]
SITE_ID = 1 # Because this error https://stackoverflow.com/questions/35388637/runtimeerror-model-class-django-contrib-sites-models-site-doesnt-declare-an-ex
RQ_SHOW_ADMIN_LINK = True 
RQ_QUEUES = {
    'default': {
        'HOST': 'redis-scheduler' if bool(os.environ.get("inDocker", False)) else "localhost",
        'PORT': 6379,
        'DB': 0,
       # 'PASSWORD': 'some-password',
        'DEFAULT_TIMEOUT': 259200,
        'ASYNC' : True if bool(os.environ.get("inDocker", False)) else False
    }
}

SINGLEONTOLOGYRETRIEVETIMOUT = 100 # Timeout until the download of a single ontology file gets aborted.
# The URL of the OPI (Ontology Programming Interface)
OPI = "opi:8080" if bool(os.environ.get("inDocker", False)) else "localhost:8085"  #"localhost:8080/ROOT"
OPITIMEOUT = 300 # Seconds until a connection request to OPI times out.
OPIRETRIES = 1 # How often the service is allows to give OPI another try.

# Size-Limits for analysis. If An ontology is larger than 0,3mb, deactivate ClassMetrics and Reasoner, because the computational
# time is enormous.

ONTOLOGYLIMIT = -1
REASONINGLIMIT = 348576
CLASSMETRICSLIMIT = 348576

# REST_FRAMEWORK = {
#     'DEFAULT_PARSER_CLASSES': [
#         'rest_framework_xml.parsers.XMLParser',
#     ],
#     'DEFAULT_RENDERER_CLASSES': [
#         'rest_framework_xml.renderers.XMLRenderer',
#     ],
# }
MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'corsheaders.middleware.CorsMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]
CORS_ORIGIN_ALLOW_ALL = True
ROOT_URLCONF = 'ontoMetricsAPI.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': ['templates'],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'ontoMetricsAPI.wsgi.application'

EMAIL_BACKEND = 'django.core.mail.backends.smtp.EmailBackend'
NEWSLETTER_THUMBNAIL = 'sorl-thumbnail'
EMAIL_USE_TLS = True
EMAIL_HOST = 'smtp.office365.com'
EMAIL_HOST_USER = 'neontometric@outlook.com'
EMAIL_HOST_PASSWORD = os.environ.get("outlookApp")
EMAIL_PORT = 587


# Database
# https://docs.djangoproject.com/en/3.0/ref/settings/#databases

# DATABASES = {
#     'default': {
#         'ENGINE': 'django.db.backends.sqlite3',
#         'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
#     }
#  }
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'OPTIONS': {'charset': 'utf8mb4'},
        'NAME': 'neontometrics',
        'USER': 'neontometrics',
        'PASSWORD':os.environ['db_password'],
        # For the alignment of Docker intergration & windows development
        'HOST': 'neontometrics_db' if bool(os.environ.get("inDocker", False)) else "localhost",
        'PORT': 3306 if bool(os.environ.get("inDocker", False)) else 3366,
    }
}


# Password validation
# https://docs.djangoproject.com/en/3.0/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


# Internationalization
# https://docs.djangoproject.com/en/3.0/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True

DEFAULT_AUTO_FIELD = 'django.db.models.AutoField'
# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/3.0/howto/static-files/

STATIC_URL = 'http://neontometrics.informatik.uni-rostock.de/djangostatic/' 
STATIC_ROOT = os.path.join(BASE_DIR, 'staticfiles')

# The standard mysql driver of django depends on a C++ Extension.
# Using PyMySql instead resolves compoatibility issues and enables the same behavior in 
# Docker and the local development area.
pymysql.version_info = (1, 4, 2, "final", 0)
pymysql.install_as_MySQLdb()

# Graphene
GRAPHENE = {
    'SCHEMA': 'rest.graqhql.schema',
    'RELAY_CONNECTION_MAX_LIMIT': 10000,
}
