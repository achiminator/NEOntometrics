FROM ghcr.io/cirruslabs/flutter:3.19.6 AS flutterbuild

COPY . /frontend
WORKDIR /frontend
RUN flutter build web

FROM python:3.10
COPY --from=flutterbuild /frontend/build/web /web
WORKDIR /web
ENTRYPOINT python3 -m http.server