(ns ratel.core
  "This namespace provides functions for loading big data to CartoDB."
  (:use [clojure.contrib.shell-out :only (sh)]
        [clojure.string :only (split)])
  (:require [clojure.java.io :as io])
  (:import [org.gdal.gdal]))

(defn- ogr2ogr-cmd
  "Return vector of ogr2ogr arguments to convert input shapefile to CSV file in
   output directory that must be different from directory containing input."
  [input output & {:keys [explode separator overwrite debug],
                   :or {explode false separator "TAB" overwrite true debug false}}]
  {:pre [(and input output)]}
  (let [cmd ["ogr2ogr"]
        cmd (if overwrite (conj cmd "-overwrite") cmd)
        cmd (if explode (conj cmd "-explodecollections") cmd)
        cmd (conj cmd "-f" "CSV" output input "-nln" "ouput")
        cmd (conj cmd "-lco" "GEOMETRY=AS_WKT" "-lco" (str "SEPARATOR=" separator))]
    (if debug
      (println cmd)
      [cmd :return-map true])
    cmd))

(defn shp->tsv
  "Convert supplied input Shapefile to a CSV file in output directory."
  [input output & {:keys [explode separator overwrite debug]
                  :or {explode false separator "TAB" overwrite true debug false}}]
  (let [cmd (ogr2ogr-cmd input output :explode explode :separator separator
                         :overwrite overwrite :debug debug)
        delete-silently true]
    (io/delete-file output delete-silently)
    (println (apply sh cmd))
    cmd))

(comment
  (let [input (->> (io/resource "shapefile/california_pass_export.shp") .getPath)
        output (str (->> (io/resource "shapefile") .getPath) "/output.tsv")]
    (shp->tsv input output)))

