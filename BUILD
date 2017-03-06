package(default_visibility = ["//visibility:public"])

java_binary(
    name = "hop",
    srcs = glob(["**/*.java"]),
    main_class = "Main",
    deps = [
        ":lib",
        "//3rdpartydeps-java:com_sparkjava_spark_core",
        "//3rdpartydeps-java:io_reactivex_rxjava2_rxjava",
    ],
)

# library component. imported with: //hop:lib
java_library(
    name = "lib",
    srcs = glob(
        ["**/*.java"],
        exclude = ["**/Main.java"],
    ),
    deps = [
        "//3rdpartydeps-java:io_reactivex_rxjava2_rxjava",
        "//3rdpartydeps-java:org_eclipse_jetty_websocket_websocket_api",
    ],
)
