# liberator-util

The entire point of this library is to allow multiple projects that use liberator to safely render maps and seqs to the same mime type using different encoders, without conflicting each other.

## Usage

To follow along with this demonstration, add `[puppetlabs/liberator-util "0.1.0"]` and `[ring-mock "0.1.5"]` to your dependencies, and then fire up a repl.

First, we need to install a dispatcher that extends liberator's representation multimethod to look for a dynamically-scoped encoder for the mime-type, falling back to the provided default if one cannot be found:

```clj
(require '[puppetlabs/liberator-util.representation :refer [install-map-representation-dispatcher! map-encoder-wrapper]]
         '[cheshire.core :as json])

(def json-mime "application/json")

(install-map-representation-dispatcher! json-mime json/encode)
```

Now, let's define a ring handler using liberator.

```clj
(require '[liberator.core :refer [resource]])

(def my-handler (resource :available-media-types [json-mime] :handle-ok (fn [ctx] {:foo-bar "baz"})))
```

Once we have a handler, we can generate middleware that sets a custom encoder for the json mime-type and wrap our handler in this middleware.

```clj
(require '[clojure.string :as str])

(defn loud-key [k] (-> k name str/upper-case))

(def wrap-loud-encoder (map-encoder-wrapper json-mime (fn [m] (json/encode m {:key-fn loud-key}))))

(def loud-handler (wrap-loud-encoder my-handler))
```

And then test out the handler with and without the custom-encoder middleware.

```clj
(require '[ring.mock.request :refer [request]])

(-> (loud-handler (request :get "/foo")) :body)
;; => "{\"FOO-BAR\":\"baz\"}"

;; contrast with the unwrapped handler's output
(-> (my-handler (request :get "/foo")) :body)
;; => "{\"foo-bar\":\"baz\"}"
```

## License

Copyright © 2014 Puppet Labs

Distributed under the Eclipse Public License 1.0.
