(ns ratel.core
  "This namespace provides functions for loading big data to CartoDB."
  (:use [ratel.fs :as fs]
        [clojure.contrib.shell-out :only (sh)])
  (:require [clojure.java.io :as io]
            [clojure.string :as s :only (replace)]
            [clojure.data.csv :as csv])
  (:import [org.gdal.gdal]
           [java.io File FilenameFilter]))

(defn- ogr2ogr-cmd
  "Return vector of ogr2ogr arguments to convert input shapefile to CSV file in
   output directory that must be different from directory containing input."
  [input output & {:keys [explode separator overwrite debug srs name],
                   :or {explode false separator "TAB" overwrite true
                        debug false srs "EPSG:4326" name "output"}}]
  {:pre [(and input output)]}
  (let [cmd ["ogr2ogr"]
        cmd (if overwrite (conj cmd "-overwrite") cmd)
        cmd (if explode (conj cmd "-explodecollections") cmd)
        cmd (conj cmd "-t_srs" srs)
        cmd (conj cmd "-f" "CSV" output input)
        cmd (conj cmd "-nln" name)
        cmd (conj cmd "-lco" "GEOMETRY=AS_WKT" "-lco" (str "SEPARATOR=" separator))]
    (if debug
      (println cmd)
      [cmd :return-map true])
    cmd))

(defn Job
  "Convert supplied shapefile input to CSV and append its contents to file."
  [append-file output input]
  (let [output (str output "/output")
        csv-path (->> (shp->tsv input output :explode true) .getPath)
        csv-file (io/reader csv-path)]
    (csv/write-csv append-file
                   (csv/read-csv csv-file :separator \tab) :separator \tab)))

(defn dir->csv
  "Convert supplied directory of Shapefiles to a single CSV file at path."
  [dir path output-dir]
  (let [shp-pattern (str dir "/*.shp")
        files (map #(.getPath %) (fs/glob shp-pattern))
        append-file (io/writer path :append true)]
    (pmap (partial Job append-file output-dir) files)))

(defn shp->csv
  "Convert supplied input Shapefile to a CSV file in output directory, returning
  its path."
  [input output & {:keys [explode separator overwrite debug srs],
                   :or {explode false separator "TAB" overwrite true
                        debug false srs "EPSG:4326"}}]
  (let [name (s/replace (->> (io/file input) .getName) ".shp" "")
        cmd (ogr2ogr-cmd input output :explode explode :separator separator
                         :overwrite overwrite :srs srs :debug debug :name name)
        delete-silently true
        output-path (io/file output (str name ".csv"))]
    (io/delete-file output delete-silently)
    (apply sh cmd)
    output-path))

(comment
  (let [input (->> (io/resource "shapefile/california_pass_export.shp") .getPath)
        output (->> (io/resource "shapefile/output") .getPath)]
    (shp->tsv input output :explode true)))
