(ns ddl.diff.mysql
  (:require [clojure.java.jdbc :as j]
            [clojure.data :refer [diff]]))

(defn ddl [mysql-db]
  (->> (j/query mysql-db ["show tables"])
       (map (comp second first seq))
       (map (fn [table-name]
              (j/query mysql-db [(str "show create table "
                                      table-name)])))
       (apply concat)
       (map (juxt :table (keyword "create table")))
       (into {})))

(defn ddl-diff [db1 db2 ignore-tables]
  (diff
   (apply dissoc [(ddl db1)
                  ignore-tables])
   (apply dissoc [(ddl db2)
                  ignore-tables])))
