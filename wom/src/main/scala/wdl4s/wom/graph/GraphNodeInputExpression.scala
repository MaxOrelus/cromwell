package wdl4s.wom.graph

import lenthall.validation.ErrorOr.ErrorOr
import wdl4s.wdl.types.WdlType
import wdl4s.wom.expression.WomExpression
import wdl4s.wom.graph.GraphNode.GraphNodeSetter
import wdl4s.wom.graph.GraphNodePort.OutputPort

/**
  * This is enough information for a GraphNode to build an InstantiatedExpression for an input.
  * Differences:
  * - This one remembers which input the expression is being assigned to.
  * - InstantiatedExpression has created InputPorts for the expression inputs. This one only has references to OutputPorts.
  */
case class GraphNodeInputExpression(inputName: String, expression: WomExpression, inputMapping: Map[String, OutputPort]) {

  /**
    * Instantiate the expression and connect its input ports to the appropriate graphNode.
    */
  private[graph] def instantiateExpression(graphNodeSetter: GraphNodeSetter): ErrorOr[InstantiatedExpression] = InstantiatedExpression.linkWithInputs(graphNodeSetter, expression, inputMapping)

  /**
    * Runs instantiateExpression and returns it tupled with the inputName.
    */
  private[graph] def instantiateExpressionWithInputName(graphNodeSetter: GraphNodeSetter): ErrorOr[(String, InstantiatedExpression)] = instantiateExpression(graphNodeSetter) map { (inputName, _) }
  private[graph] lazy val evaluateType: ErrorOr[WdlType] = expression.evaluateType(inputMapping.map { case (name, port) => (name, port.womType) })
}
