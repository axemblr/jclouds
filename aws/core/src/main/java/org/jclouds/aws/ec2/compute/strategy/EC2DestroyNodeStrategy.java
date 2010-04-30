/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute.strategy;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule.GetRegionFromNodeOrDefault;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2DestroyNodeStrategy implements DestroyNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;
   protected final Predicate<RunningInstance> instanceStateTerminated;
   protected final GetRegionFromNodeOrDefault getRegionFromNodeOrDefault;
   protected final GetNodeMetadataStrategy getNodeMetadataStrategy;

   @Inject
   protected EC2DestroyNodeStrategy(EC2Client ec2Client,
            @Named("TERMINATED") Predicate<RunningInstance> instanceStateTerminated,
            GetRegionFromNodeOrDefault getRegionFromNodeOrDefault,
            GetNodeMetadataStrategy getNodeMetadataStrategy) {
      this.ec2Client = ec2Client;
      this.instanceStateTerminated = instanceStateTerminated;
      this.getRegionFromNodeOrDefault = getRegionFromNodeOrDefault;
      this.getNodeMetadataStrategy = getNodeMetadataStrategy;
   }

   @Override
   public boolean execute(ComputeMetadata metadata) {
      NodeMetadata node = metadata instanceof NodeMetadata ? NodeMetadata.class.cast(metadata)
               : getNodeMetadataStrategy.execute(metadata);
      String region = getRegionFromNodeOrDefault.apply(node);

      ec2Client.getInstanceServices().terminateInstancesInRegion(region, node.getId());
      return instanceStateTerminated.apply(Iterables.getOnlyElement(Iterables.concat(ec2Client
               .getInstanceServices().describeInstancesInRegion(region, node.getId()))));
   }

}