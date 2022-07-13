FROM cirrusci/flutter:latest AS flutterbuild

COPY . /frontend
WORKDIR /frontend
RUN flutter build web

FROM python:3
COPY --from=flutterbuild /frontend/build/web /web
WORKDIR /web
ENTRYPOINT python3 -m http.server