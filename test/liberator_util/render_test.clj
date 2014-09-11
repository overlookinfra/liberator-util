(ns liberator-util.render-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [liberator.core :refer [resource]]
            [liberator-util.render :refer :all]
            [ring.mock.request :refer [request]]))

(def json-mime "application/json")

(install-map-representation-dispatcher! json-mime json/encode)
(install-seq-representation-dispatcher! json-mime json/encode)

(deftest custom-map-encoding
  (let [the-map {:foo "bar"}
        bare-handler (resource :available-media-types [json-mime]
                               :handle-ok (fn [_] the-map))]

    (testing "when a handler is wrapped with a custom map encoder"
      (let [custom-rep "custom-map"
            wrap-map-encoder (map-encoder-wrapper json-mime (fn [data] custom-rep))
            wrapped-handler (wrap-map-encoder bare-handler)
            {:keys [status body]} (wrapped-handler (request :get "/foo"))]
        (testing "the request succeeds"
          (is (= 200 status)))
        (testing "the custom map encoder is used"
          (is (= custom-rep body)))))

    (testing "when a handler is not wrapped with a custom map encoder"
      (let [{:keys [status body]} (bare-handler (request :get "/foo"))]
        (testing "the request succeeds"
          (is (= 200 status)))
        (testing "the default encoder is used as a fallback"
          (is (= (json/encode the-map) body)))))))

(deftest custom-seq-encoding
  (let [the-seq [{:foo "bar"} {:baz "quux"}]
        bare-handler (resource :available-media-types [json-mime]
                               :handle-ok (fn [_] the-seq))]

    (testing "when a handler is wrapped with a custom seq encoder"
      (let [custom-rep "custom-seq"
            wrap-seq-encoder (seq-encoder-wrapper json-mime (fn [data] custom-rep))
            wrapped-handler (wrap-seq-encoder bare-handler)
            {:keys [status body]} (wrapped-handler (request :get "/foo"))]
        (testing "the request succeeds"
          (is (= 200 status)))
        (testing "the custom seq encoder is used"
          (is (= custom-rep body)))))

    (testing "when a handler is not wrapped with a custom seq encoder"
      (let [{:keys [status body]} (bare-handler (request :get "/foo"))]
        (testing "the request succeeds"
          (is (= 200 status)))
        (testing "the default encoder is used as a fallback"
          (is (= (json/encode the-seq) body)))))))
