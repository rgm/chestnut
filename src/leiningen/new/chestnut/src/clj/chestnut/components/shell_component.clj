(ns {{project-ns}}.components.shell-component
  (:require [com.stuartsierra.component :as component]
            [clojure.string :as str]))

(defn spawn [command]
  "starts a shell process, returns the java.lang.Process wrapped in a future"
  (let [builder (ProcessBuilder. command)]
    (future (.start builder))))

(defn kill [p]
  "p is a future-wrapped java.lang.Process"
  (.destroy @p))

(defrecord ShellComponent [command]
  component/Lifecycle
  (start [this]
    (if-not (:shell-process this)
      (do
        (println "Shell command: Starting" (str/join " " command))
        (assoc this :shell-process (spawn command)))
      this))
  (stop [this]
    (when-let [p (:shell-process this)]
      (println "Shell command: Stopping" (str/join " " command))
      (kill p))
    (assoc this :shell-process nil)))

(defn shell-component [& cmd]
  (->ShellComponent cmd))
