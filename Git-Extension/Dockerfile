FROM python:3
ENV PYTHONUNBUFFERED=1
RUN mkdir /code
WORKDIR /code
EXPOSE 8000
#COPY requirements.txt /code/
RUN pip install pipenv
COPY . /code/
RUN pipenv install --system --deploy
#RUN python manage.py makemigrations migrate 
#ENTRYPOINT ["python", "manage.py", "runserver", "0.0.0.0:8000"] 