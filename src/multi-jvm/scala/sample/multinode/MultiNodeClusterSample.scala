//#package
package sample.multinode
//#package

//#config

import akka.cluster.Cluster
import akka.remote.testconductor.RoleName
import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory


object MultiNodeClusterSampleConfig extends MultiNodeConfig {
  val node1 : RoleName = role("node1")
  val node2 : RoleName = role("node2")


  import akka.cluster.MultiNodeClusterSpec

  commonConfig(debugConfig(on = false).withFallback(ConfigFactory.parseString(
    "akka.cluster.auto-down-unreachable-after = 0s")).
    withFallback(MultiNodeClusterSpec.clusterConfigWithFailureDetectorPuppet))
}
//#config

//#spec
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.actor.{ Props, Actor }

class MultiNodeClusterSampleSpecMultiJvmNode1 extends MultiNodeClusterSample
class MultiNodeClusterSampleSpecMultiJvmNode2 extends MultiNodeClusterSample

object MultiNodeClusterSample {
  class Ponger extends Actor {
    def receive = {
      case "ping" => sender() ! "pong"
    }
  }
}



class MultiNodeClusterSample extends MultiNodeSpec(MultiNodeSampleConfig)
with STMultiNodeSpec with ImplicitSender {




  import MultiNodeClusterSampleConfig._
  import MultiNodeClusterSample._

  def initialParticipants = roles.size

  def cluster: Cluster = Cluster(system)

  "A MultiNodeSample" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "send to and receive from a remote node" in {
      runOn(node1) {
        enterBarrier("deployed")
//        cluster.leave(node2)
//        cluster.leave(node2)


      }



      enterBarrier("finished")
    }
  }
}
//#spec
