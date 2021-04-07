(ns dottiesbot.lambda_handler
  (:gen-class
   :methods [[handler [String com.amazonaws.services.lambda.runtime.Context] String]]))

(defn -handler [this s ctx]
  (str "Hello " s "!"))
