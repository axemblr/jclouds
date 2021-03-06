/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rds.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rds.binders.BindInstanceRequestToFormParams;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.domain.InstanceRequest;
import org.jclouds.rds.functions.InstancesToPagedIterable;
import org.jclouds.rds.functions.ReturnNullOnStateDeletingNotFoundOr404;
import org.jclouds.rds.options.ListInstancesOptions;
import org.jclouds.rds.xml.DescribeDBInstancesResultHandler;
import org.jclouds.rds.xml.InstanceHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see InstanceApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface InstanceAsyncApi {
   /**
    * @see InstanceApi#create
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @FormParams(keys = ACTION, values = "CreateDBInstance")
   ListenableFuture<Instance> create(@FormParam("DBInstanceIdentifier") String id,
            @BinderParam(BindInstanceRequestToFormParams.class) InstanceRequest instanceRequest);

   /**
    * @see InstanceApi#createInAvailabilityZone
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @FormParams(keys = ACTION, values = "CreateDBInstance")
   ListenableFuture<Instance> createInAvailabilityZone(@FormParam("DBInstanceIdentifier") String id,
            @BinderParam(BindInstanceRequestToFormParams.class) InstanceRequest instanceRequest,
            @FormParam("AvailabilityZone") String availabilityZone);

   /**
    * @see InstanceApi#createMultiAZ
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @FormParams(keys = { ACTION, "MultiAZ" }, values = { "CreateDBInstance", "true" })
   ListenableFuture<Instance> createMultiAZ(@FormParam("DBInstanceIdentifier") String id,
            @BinderParam(BindInstanceRequestToFormParams.class) InstanceRequest instanceRequest);

   /**
    * @see InstanceApi#get()
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBInstances")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Instance> get(@FormParam("DBInstanceIdentifier") String id);

   /**
    * @see InstanceApi#list()
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBInstancesResultHandler.class)
   @Transform(InstancesToPagedIterable.class)
   @FormParams(keys = "Action", values = "DescribeDBInstances")
   ListenableFuture<PagedIterable<Instance>> list();

   /**
    * @see InstanceApi#list(ListInstancesOptions)
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBInstancesResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBInstances")
   ListenableFuture<IterableWithMarker<Instance>> list(ListInstancesOptions options);

   /**
    * @see InstanceApi#delete()
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @ExceptionParser(ReturnNullOnStateDeletingNotFoundOr404.class)
   @FormParams(keys = { ACTION, "SkipFinalSnapshot" }, values = { "DeleteDBInstance", "true" })
   ListenableFuture<Instance> delete(@FormParam("DBInstanceIdentifier") String id);

   /**
    * @see InstanceApi#deleteAndSaveSnapshot
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstanceHandler.class)
   @ExceptionParser(ReturnNullOnStateDeletingNotFoundOr404.class)
   @FormParams(keys = ACTION, values = "DeleteDBInstance")
   ListenableFuture<Instance> deleteAndSaveSnapshot(@FormParam("DBInstanceIdentifier") String id,
            @FormParam("FinalDBSnapshotIdentifier") String snapshotId);
}
