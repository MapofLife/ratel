#!/bin/bash

scp -i ~/.leiningen/id_rsa pom.xml gdal-1.9.1.jar  clojars@clojars.org:
