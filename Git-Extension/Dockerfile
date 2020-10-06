FROM python:3
ENV PYTHONUNBUFFERED=1
RUN mkdir /code
WORKDIR /code
EXPOSE 8000
#COPY requirements.txt /code/
RUN pip install Django djangorestframework xmltodict pygit2 requests pymysql cryptography
COPY . /code/
#RUN python manage.py makemigrations migrate 
#ENTRYPOINT ["python", "manage.py", "runserver", "0.0.0.0:8000"] 