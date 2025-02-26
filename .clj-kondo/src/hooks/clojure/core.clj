(ns hooks.clojure.core
  (:require
   [clj-kondo.hooks-api :as hooks]
   [clojure.string :as str]
   [hooks.common]))

;;; TODO -- seems silly to maintain different blacklists and whitelists here than we use for the deftest `^:parallel`
;;; checker... those lists live in the Clj Kondo config file
(def ^:private symbols-allowed-in-fns-not-ending-in-an-exclamation-point
  '#{;; these toucan methods might actually set global values if it's used outside of a transaction,
     ;; but since mt/with-temp runs in a transaction, so we'll ignore them in this case.
     toucan2.core/delete!
     toucan2.core/update!
     toucan2.core/insert!
     toucan2.core/insert-returning-instance!
     toucan2.core/insert-returning-instances!
     toucan2.core/insert-returning-pk!
     toucan2.core/insert-returning-pks!
     clojure.core.async/<!!
     clojure.core.async/>!!
     clojure.core.async/alts!!
     clojure.core.async/close!
     clojure.core.async/poll!
     clojure.core.memoize/memo-clear!
     clojure.core/conj!
     clojure.core/persistent!
     clojure.core/reset!
     clojure.core/swap!
     clojure.core/volatile!
     clojure.core/vreset!
     clojure.core/vswap!
     clojure.java.jdbc/execute!
     methodical.core/add-aux-method-with-unique-key!
     methodical.core/remove-aux-method-with-unique-key!
     next.jdbc/execute!

     ;; Definitely thread safe
     metabase.test.util.dynamic-redefs/patch-vars!

     ;; TODO: most of these symbols shouldn't be here, we should go through them and
     ;; find the functions/macros that use them and make sure their names end with !
     ;; best way to do this is try remove each of these and rely on kondo output to find places where it's used
     clojure.test/grant-collection-perms!
     clojure.test/grant-collection-perms-fn!
     clojure.test/grant-perms-fn!
     clojure.test/purge-old-entries!
     clojure.test/revoke-collection-perms!
     clojure.test/save-results!
     metabase-enterprise.advanced-permissions.models.permissions/update-db-download-permissions!
     metabase-enterprise.internal-user/install-internal-user!
     metabase-enterprise.sso.integrations.saml-test/call-with-login-attributes-cleared!
     metabase.actions.actions/perform-action!
     metabase.actions.models/insert!
     metabase.analytics.snowplow-test/fake-track-event-impl!
     metabase.analytics.snowplow/track-event-impl!
     metabase.api.public-test/add-card-to-dashboard!
     metabase.channel.email-test/reset-inbox!
     metabase.channel.email/send-email!
     metabase.cmd.dump-to-h2/dump-to-h2!
     metabase.cmd.load-from-h2/load-from-h2!
     metabase.core.core/ensure-audit-db-installed!
     metabase.db.schema-migrations-test.impl/run-migrations-in-range!
     metabase.db.setup/migrate!
     metabase.db.setup/setup-db!
     metabase.db/migrate!
     metabase.db/setup-db!
     metabase.driver.mongo-test/create-database-from-row-maps!
     metabase.driver.postgres-test/create-enums-db!
     metabase.driver.postgres-test/drop-if-exists-and-create-db!
     metabase.driver.sql-jdbc.execute/execute-statement!
     metabase.indexed-entities.models.model-index/add-values!
     metabase.indexed-entities.task.index-values/job-init!
     metabase.models.collection.graph-test/clear-graph-revisions!
     metabase.models.collection.graph-test/do-with-n-temp-users-with-personal-collections!
     metabase.models.field-values/create-or-update-full-field-values!
     metabase.models.moderation-review/create-review!
     metabase.models.on-demand-test/add-dashcard-with-parameter-mapping!
     metabase.models.persisted-info/ready-database!
     metabase.models.setting-test/test-user-local-allowed-setting!
     metabase.models.setting-test/test-user-local-only-setting!
     metabase.models.setting.cache/restore-cache!
     metabase.models.setting/set!
     metabase.models.setting/validate-settings-formatting!
     metabase.permissions.models.permissions/grant-application-permissions!
     metabase.permissions.models.permissions/grant-collection-read-permissions!
     metabase.permissions.models.permissions/grant-collection-readwrite-permissions!
     metabase.permissions.models.permissions/grant-full-data-permissions!
     metabase.permissions.models.permissions/grant-native-readwrite-permissions!
     metabase.permissions.models.permissions/grant-permissions!
     metabase.permissions.models.permissions/revoke-application-permissions!
     metabase.permissions.models.permissions/revoke-data-perms!
     metabase.permissions.models.permissions/update-data-perms-graph!
     metabase.permissions.models.permissions/update-group-permissions!
     metabase.permissions.test-util/with-restored-perms!
     metabase.pulse.send/send-notifications!
     metabase.pulse.send/send-pulse!
     metabase.query-processor.streaming.interface/begin!
     metabase.query-processor.streaming.interface/finish!
     metabase.query-processor.streaming.interface/write-row!
     metabase.sample-data/try-to-extract-sample-database!
     metabase.setup.core/create-token!
     metabase.sync.core/sync-database!
     metabase.sync.sync-metadata.fields.sync-metadata/update-field-metadata-if-needed!
     metabase.sync.sync-metadata/sync-db-metadata!
     metabase.sync.util-test/sync-database!
     metabase.sync.util/store-sync-summary!
     metabase.task.persist-refresh/job-init!
     metabase.task.persist-refresh/refresh-tables!
     metabase.task.persist-refresh/schedule-persistence-for-database!
     metabase.task/delete-task!
     metabase.test.data.bigquery-cloud-sdk/execute!
     metabase.test.data.impl/copy-db-tables-and-fields!
     metabase.test.data.impl/get-or-create-database!
     metabase.test.data.impl/get-or-create-default-dataset!
     metabase.test.data.impl.get-or-create/set-test-db-permissions!
     metabase.test.data.interface/create-db!
     metabase.test.data.interface/destroy-db!
     metabase.test.data.oracle/create-user!
     metabase.test.data.oracle/drop-user!
     metabase.test.data.sql-jdbc.load-data/make-insert!
     metabase.test.data.users/clear-cached-session-tokens!
     metabase.test.initialize/do-initialization!
     metabase.test.initialize/initialize-if-needed!
     metabase.test.integrations.ldap/start-ldap-server!
     metabase.test.util.log/ensure-unique-logger!
     metabase.test.util.log/set-ns-log-level!
     metabase.test.util/do-with-temp-env-var-value!
     metabase.test.util/restore-raw-setting!
     metabase.test.util/upsert-raw-setting!
     metabase.test/initialize-if-needed!
     metabase.test/test-helpers-set-global-values!
     metabase.test/with-temp-env-var-value!
     metabase.upload-test/set-local-infile!
     metabase.util.files/create-dir-if-not-exists!
     metabase.util.ssh-test/start-mock-servers!
     metabase.util.ssh-test/stop-mock-servers!})

(defn- end-with-exclamation?
  [s]
  (str/ends-with? s "!"))

(defn- explicitly-safe? [qualified-symbol]
  (contains? symbols-allowed-in-fns-not-ending-in-an-exclamation-point qualified-symbol))

(defn- explicitly-unsafe? [config qualified-symbol]
  (contains? (get-in config [:linters :metabase/validate-deftest :parallel/unsafe]) qualified-symbol))

(defn- unsafe? [config qualified-symbol]
  (and (or (end-with-exclamation? qualified-symbol)
           (explicitly-unsafe? config qualified-symbol))
       (not (explicitly-safe? qualified-symbol))))

(defn- non-thread-safe-form-should-end-with-exclamation*
  [{[defn-or-defmacro form-name] :children, :as node} config]
  (when-not (and (:string-value form-name)
                 (end-with-exclamation? (:string-value form-name)))
    (letfn [(walk [f form]
              (f form)
              (doseq [child (:children form)]
                (walk f child)))
            (check-node [form]
              (when-let [qualified-symbol (hooks.common/node->qualified-symbol form)]
                (when (unsafe? config qualified-symbol)
                  (hooks/reg-finding!
                   (assoc (meta form-name)
                          :message (format "The name of this %s should end with `!` because it contains calls to non thread safe form `%s`. [:metabase/test-helpers-use-non-thread-safe-functions]"
                                           (:string-value defn-or-defmacro) qualified-symbol)
                          :type :metabase/test-helpers-use-non-thread-safe-functions)))))]
      (walk check-node node))
    node))

(defn non-thread-safe-form-should-end-with-exclamation
  "Used to ensure defn and defmacro in test namespace to have name ending with `!` if it's non-thread-safe.
  A function or a macro can be defined as 'not thread safe' when their funciton name ends with a `!`.

  Only used in tests to identify thread-safe/non-thread-safe test helpers. See #37126"
  [{:keys [node cljc lang config]}]
  (when (or (not cljc)
            (= lang :clj))
    (non-thread-safe-form-should-end-with-exclamation* node config))
  {:node node})

(comment
  (require '[clj-kondo.core :as clj-kondo])
  (def form (str '(defmacro a
                    [x]
                    `(fun-call x))))

  (def form "(defmacro a
           [x]
           `(some! ~x))")

  (def form "(defun f
           [x]
           (let [g! (fn [] 1)]
           (g!)))")

  (str (hooks/parse-string form))
  (hooks/sexpr (hooks/parse-string form))

  (binding [hooks/*reload* true]
    (-> form
        (with-in-str (clj-kondo/run! {:lint ["-"]}))
        :findings))

  (do (non-thread-safe-form-should-end-with-exclamation* (hooks/parse-string form)) nil))

(defn- ns-form-node->require-node [ns-form-node]
  (some (fn [node]
          (when (and (hooks/list-node? node)
                     (let [first-child (first (:children node))]
                       (and (hooks/keyword-node? first-child)
                            (= (hooks/sexpr first-child) :require))))
            node))
        (:children ns-form-node)))

(defn- lint-require-shapes [ns-form-node]
  (doseq [node (-> ns-form-node
                   ns-form-node->require-node
                   :children
                   rest)]
    (cond
      (not (hooks/vector-node? node))
      (hooks/reg-finding! (assoc (meta node)
                                 :message "All :required namespaces should be wrapped in vectors [:metabase/require-shape-checker]"
                                 :type    :metabase/require-shape-checker))

      (hooks/vector-node? (second (:children node)))
      (hooks/reg-finding! (assoc (meta node)
                                 :message "Don't use prefix forms inside :require [:metabase/require-shape-checker]"
                                 :type    :metabase/require-shape-checker)))))

(defn- lint-requires-on-new-lines [ns-form-node]
  (let [[require-keyword first-require] (-> ns-form-node
                                            ns-form-node->require-node
                                            :children)]
    (when-let [require-keyword-line (:row (meta require-keyword))]
      (when-let [first-require-line (:row (meta first-require))]
        (when (= require-keyword-line first-require-line)
          (hooks/reg-finding! (assoc (meta first-require)
                                     :message "Put your requires on a newline from the :require keyword [:metabase/require-shape-checker]"
                                     :type    :metabase/require-shape-checker)))))))

(defn- require-node->namespace-symb-nodes [require-node]
  (let [[_ns & args] (:children require-node)]
    (into []
          ;; prefixed namespace forms are NOT SUPPORTED!!!!!!!!1
          (keep (fn [node]
                  (cond
                    (hooks/vector-node? node)
                    ;; propagate the metadata attached to this vector in case there's a `:clj-kondo/ignore` form.
                    (let [symbol-node (first (:children node))]
                      (hooks.common/merge-ignored-linters symbol-node require-node node))

                    ;; this should also be dead code since we require requires to be vectors
                    (hooks/token-node? node)
                    (hooks.common/merge-ignored-linters node require-node)

                    :else
                    (printf "Don't know how to figure out what namespace is being required in %s\n" (pr-str node)))))
          args)))

(defn- ns-form-node->ns-symb [ns-form-node]
  (some-> (some (fn [node]
                  (when (and (hooks/token-node? node)
                             (not= (hooks/sexpr node) 'ns))
                    node))
                (:children ns-form-node))
          hooks/sexpr))

(defn- module
  "E.g.

    (module 'metabase.qp.middleware.wow) => 'metabase.qp"
  [ns-symb]
  (some-> (re-find #"^metabase\.[^.]+" (str ns-symb)) symbol))

(defn- ignored-namespace? [ns-symb config]
  (some
   (fn [pattern-str]
     (re-find (re-pattern pattern-str) (str ns-symb)))
   (:ignored-namespace-patterns config)))

(defn- module-api-namespaces
  "Set API namespaces for a given module. `:any` means you can use anything, there are no API namespaces for this
  module (yet). If unspecified, the default is just the `<module>.core` namespace."
  [module config]
  (let [module-config (get-in config [:api-namespaces module])]
    (cond
      (= module-config :any)
      nil

      (set? module-config)
      module-config

      :else
      #{(symbol (str module ".core"))})))

(defn- lint-modules [ns-form-node config]
  (let [ns-symb (ns-form-node->ns-symb ns-form-node)]
    (when-not (ignored-namespace? ns-symb config)
      (when-let [current-module (module ns-symb)]
        (let [allowed-modules               (get-in config [:allowed-modules current-module])
              required-namespace-symb-nodes (-> ns-form-node
                                                ns-form-node->require-node
                                                require-node->namespace-symb-nodes)]
          (doseq [node  required-namespace-symb-nodes
                  :when (not (contains? (hooks.common/ignored-linters node) :metabase/ns-module-checker))
                  :let  [required-namespace (hooks/sexpr node)
                         required-module    (module required-namespace)]
                  ;; ignore stuff not in a module i.e. non-Metabase stuff.
                  :when required-module
                  :let  [in-current-module? (= required-module current-module)]
                  :when (not in-current-module?)
                  :let  [allowed-module?           (or (= allowed-modules :any)
                                                       (contains? (set allowed-modules) required-module))
                         module-api-namespaces     (module-api-namespaces required-module config)
                         allowed-module-namespace? (or (empty? module-api-namespaces)
                                                       (contains? module-api-namespaces required-namespace))]]
            (when-let [error (cond
                               (not allowed-module?)
                               (format "Module %s should not be used in the %s module. [:metabase/ns-module-checker :allowed-modules %s]"
                                       required-module
                                       current-module
                                       current-module)

                               (not allowed-module-namespace?)
                               (format "Namespace %s is not an allowed external API namespace for the %s module. [:metabase/ns-module-checker :api-namespaces %s]"
                                       required-namespace
                                       required-module
                                       required-module))]
              (hooks/reg-finding! (assoc (meta node)
                                         :message error
                                         :type    :metabase/ns-module-checker)))))))))

(defn lint-ns [x]
  (doto (:node x)
    lint-require-shapes
    lint-requires-on-new-lines
    (lint-modules (get-in x [:config :linters :metabase/ns-module-checker])))
  x)

(defn- check-arglists [report-node arglists]
  (letfn [(reg-bad-arglists! []
            (hooks/reg-finding!
             (assoc (meta report-node)
                    :message ":arglists should be a quoted list of vectors [:metabase/check-defmulti-arglists]"
                    :type :metabase/check-defmulti-arglists)))
          (reg-bad-arg! []
            (hooks/reg-finding!
             (assoc (meta report-node)
                    :message ":arglists should contain actual arg names, not underscore (unused) symbols [:metabase/check-defmulti-arglists]"
                    :type    :metabase/check-defmulti-arglists)))
          (underscore-arg? [arg]
            (and (symbol? arg)
                 (str/starts-with? arg "_")))
          (check-arglist [arglist]
            (cond
              (not (vector? arglist))        (reg-bad-arglists!)
              (some underscore-arg? arglist) (reg-bad-arg!)))]
    (if-not (and (seq? arglists)
                 (= (first arglists) 'quote)
                 (seq (second arglists)))
      (reg-bad-arglists!)
      (let [[_quote arglists] arglists]
        (doseq [arglist arglists]
          (check-arglist arglist))))))

(defn- defmulti-check-for-arglists-metadata
  "Make sure a [[defmulti]] has an attribute map with `:arglists` metadata."
  [node]
  (let [[_defmulti _symb & args] (:children node)
        [_docstring & args]      (if (hooks/string-node? (first args))
                                   args
                                   (cons nil args))
        attr-map                 (when (hooks/map-node? (first args))
                                   (first args))
        arglists                 (some-> attr-map hooks/sexpr :arglists seq)]
    (if (not (seq? arglists))
      (hooks/reg-finding!
       (assoc (meta node)
              :message "All defmultis should have an attribute map with :arglists metadata. [:metabase/check-defmulti-arglists]"
              :type    :metabase/check-defmulti-arglists))
      (check-arglists attr-map arglists))))

(defn lint-defmulti [x]
  (defmulti-check-for-arglists-metadata (:node x))
  x)
