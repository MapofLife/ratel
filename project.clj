(defproject ratel "0.1.0-SNAPSHOT"
  :description "Savagely attacking MOL data, crazy nastyass style."
  :repositories {"conjars" "http://conjars.org/repo/"}
  :source-path "src/clj"
  :resources-path "resources"
  :dev-resources-path "dev"
  :jvm-opts ["-XX:MaxPermSize=256M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :plugins [[lein-swank "1.4.4"]
            [lein-midje "1.0.8"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [cascalog "1.9.0"]                 
                 [cartodb-clj "1.1.1-SNAPSHOT"]
                 [ratel/gdal "1.9.1"]
                 [org.clojure/data.csv "0.1.2"]
                 [net.lingala.zip4j/zip4j "1.3.1"]
                 [com.google.guava/guava "12.0"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]
                     [midje "1.4.0"]])
