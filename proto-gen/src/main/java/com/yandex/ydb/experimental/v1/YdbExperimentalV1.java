// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: kikimr/public/api/grpc/draft/ydb_experimental_v1.proto

package tech.ydb.experimental.v1;

public final class YdbExperimentalV1 {
  private YdbExperimentalV1() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n6kikimr/public/api/grpc/draft/ydb_exper" +
      "imental_v1.proto\022\023Ydb.Experimental.V1\032/k" +
      "ikimr/public/api/protos/ydb_experimental" +
      ".proto2\253\003\n\023ExperimentalService\022W\n\nUpload" +
      "Rows\022#.Ydb.Experimental.UploadRowsReques" +
      "t\032$.Ydb.Experimental.UploadRowsResponse\022" +
      "Z\n\013ReadColumns\022$.Ydb.Experimental.ReadCo" +
      "lumnsRequest\032%.Ydb.Experimental.ReadColu" +
      "mnsResponse\022l\n\021GetShardLocations\022*.Ydb.E" +
      "xperimental.GetShardLocationsRequest\032+.Y",
      "db.Experimental.GetShardLocationsRespons" +
      "e\022q\n\022ExecuteStreamQuery\022+.Ydb.Experiment" +
      "al.ExecuteStreamQueryRequest\032,.Ydb.Exper" +
      "imental.ExecuteStreamQueryResponse0\001B \n\036" +
      "tech.ydb.experimental.v1b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          tech.ydb.experimental.ExperimentalProtos.getDescriptor(),
        }, assigner);
    tech.ydb.experimental.ExperimentalProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}