<#-- -------------------- constants------------------------------/#-->
<#assign DEBUG_MODE=1>
<#assign BLOCKTYPE_ROUTER=0 >
<#assign BLOCKTYPE_PE_WR=1 >
<#-- ----------------- constants end------------------------------/#-->
<#-- ----------------------- maps --------------------------------/#-->
<#assign portIdToIndex = {"":""}>
<#assign portIdToRouter = {"":""}>
<#-- --------------------- maps end--------------------------------/#-->
<#-- -------------------- functions -------------------------------/#-->
<#function getLinkWithSrcPortId srcPortId>
<#list getSystem().getLinks().getLink() as link>
  <#if link.getSourcePortId() == srcPortId>
    <#return link>
  </#if>
</#list>
<#return {"":""}>
</#function>
<#function getLinkWithDstPortId dstPortId>
<#list getSystem().getLinks().getLink() as link>
  <#if link.getDestinationPortId() == dstPortId>
    <#return link>
  </#if>
</#list>
<#return {"":""}>
</#function>
<#function getBlockWithPortId portId>
  <#list getSystem().getBlocks().getBlock() as block>
    <#list block.getPorts().getPort() as port>
    <#if port.getId() == portId>
        <#return block>
    </#if>
    </#list>
  </#list>
  <#return {"":""}>
</#function>
<#-- ------------------ functions end-------------------------------/#-->
<#-- ------------------ start texts --------------------------------/#-->
#include <iostream>
#include <fstream>
#include <sstream>

#include "Util.h"
#include "TrafficAnalyzer.h"

int sc_main(int argc, char* argv[]) {
  sc_set_time_resolution(1, SC_NS);
  //float load_scale_factor = 1.0/116873;
  float load_scale_factor = 1.0/16000;
  
  string logFileName = "default_log.txt";
  if(argc == 2) {
    string param1 = argv[1];
    load_scale_factor = std::stod(param1);    
  } else if(argc == 3){
    string param1 = argv[1];
    logFileName = argv[2];
    load_scale_factor = std::stod(param1);
  }

  sc_clock clk("clock", 10, SC_NS);
  sc_signal<bool> rst;
  
<#list getSystem().getBlocks().getBlock() as block>
  <#if block.getBlockType() == BLOCKTYPE_PE_WR>
  ProcessingElementWR* _${block.getName()?lower_case} = new ProcessingElementWR("${block.getName()}", ${block.getId()}0000, 4, 8);
  _${block.getName()?lower_case}->clk(clk);
  _${block.getName()?lower_case}->rst(rst);
  </#if>  
</#list>
  
<#list getSystem().getBlocks().getBlock() as block>
  <#if block.getBlockType() == BLOCKTYPE_ROUTER>
  <#assign routerInfoName=block.getName()?lower_case+"_info">
  RouterInfo ${routerInfoName};
  	<#assign inputPortIndex = 0>
  	<#assign outputPortIndex = 0>
  	<#list block.getPorts().getPort() as port>
		<#if port.writeDataWidth??>
  ${routerInfoName}.addOutputPort(1);
  ${routerInfoName}.setOutputChannelDeepth(${outputPortIndex}, 0, 8);
  ${routerInfoName}.setOutputChannelCreditNumber(${outputPortIndex}, 0, 8);
  			<#assign portIdToIndex = portIdToIndex + {port.getId()?string:outputPortIndex} >
  			<#assign portIdToRouter = portIdToRouter + {port.getId()?string:block} >
			<#assign outputPortIndex = outputPortIndex + 1 >
  		<#else>
  ${routerInfoName}.addInputPort(1);
  ${routerInfoName}.setInputChannelDeepth(${inputPortIndex}, 0, 8);
  			<#assign portIdToIndex=portIdToIndex+{port.getId()?string:inputPortIndex} >
  			<#assign portIdToRouter = portIdToRouter + {port.getId()?string:block} >
			<#assign inputPortIndex = inputPortIndex + 1 >
		</#if>
	</#list>
	
  </#if>    
</#list>
<#assign usecase = getCommunication().getUseCase()?first>
  <#list usecase.getInitiators().getInitiator() as initiator>
    <#list initiator.getTargets().getTarget() as target>
      <#list target.getFlows().getFlow() as flow>
  _${initiator.getName()?lower_case}->setDataFlowLoad(${flow.getId()}, ${flow.getSustainedBandwidth()?c}*load_scale_factor); 
      </#list>
    </#list>
  </#list>        

<#list getSystem().getRoutes().getSourceRouting().getRoute() as route>
  <#assign previousOutputPortId=route.getSourceId() >
  <#list route.getSwitchOutputPortIds().getSwitchOutputPortId() as outputPortId>
  <#assign inputPortId = getLinkWithSrcPortId(previousOutputPortId).getDestinationPortId()>
  <#assign currentRouter = portIdToRouter[inputPortId?string]>
  ${currentRouter.getName()?lower_case}_info.addRouteEntry(${route.getFlowId()}, ${portIdToIndex[inputPortId?string]}, 0, ${portIdToIndex[outputPortId?string]}, 0);
  <#assign previousOutputPortId = outputPortId>
  </#list>
  
</#list>
<#list getSystem().getBlocks().getBlock() as block>
  <#if block.getBlockType() == BLOCKTYPE_ROUTER>
  <#assign routerInfoName=block.getName()+"_info">
  Router* ${block.getName()?lower_case} = new Router("${block.getName()}", ${routerInfoName?lower_case});
  ${block.getName()?lower_case}->clk(clk); 
  ${block.getName()?lower_case}->rst(rst);
  </#if>
</#list>

<#list getSystem().getLinks().getLink() as link>
  <#assign srcPortId = link.getSourcePortId() >
  <#assign dstPortId = link.getDestinationPortId() >
  <#assign srcBlock = getBlockWithPortId(srcPortId)>
  <#assign dstBlock = getBlockWithPortId(dstPortId)>
  <#if srcBlock.getBlockType() == BLOCKTYPE_PE_WR >
  connectPeWROutportToRouter(_${srcBlock.getName()?lower_case}, ${dstBlock.getName()?lower_case}, ${portIdToIndex[dstPortId?string]});
  <#else>
    <#if dstBlock.getBlockType() == BLOCKTYPE_PE_WR >
  connectPeWRInportToRouter(_${dstBlock.getName()?lower_case}, ${srcBlock.getName()?lower_case}, ${portIdToIndex[srcPortId?string]});
  	<#else>
  	  <#if srcBlock.getBlockType() == BLOCKTYPE_ROUTER && dstBlock.getBlockType() == BLOCKTYPE_ROUTER >
  connectRouters(${srcBlock.getName()?lower_case}, ${portIdToIndex[srcPortId?string]}, ${dstBlock.getName()?lower_case}, ${portIdToIndex[dstPortId?string]});
  	  </#if>   
    </#if>
  </#if> 
</#list>  
          
  rst.write(true);
  sc_start(100, SC_NS );
  rst.write(false);
  sc_start(50000, SC_NS );
  
  cout<<"simulation finish !"<<endl;
  
  <#if DEBUG_MODE == 1>
  TrafficAnalyzer analyzer;
  <#list getSystem().getBlocks().getBlock() as block>
    <#if block.getBlockType() == BLOCKTYPE_PE_WR>
  analyzer.addProducerLogFileName("log_files/${block.getName()}.producer.txt");
    </#if>  
  </#list>
  
  <#list getSystem().getBlocks().getBlock() as block>
    <#if block.getBlockType() == BLOCKTYPE_PE_WR>
  analyzer.addConsumerLogFileName("log_files/${block.getName()}.consumer.txt");
    </#if>  
  </#list>
  
  analyzer.readFiles();
  // analyzer.analyzeOneFlow(2);
  analyzer.analyzeAllFlows();
  analyzer.reportAllFlows();
  
  ofstream average_latency_result;
  average_latency_result.open(logFileName, std::ofstream::out | std::ofstream::app);
  average_latency_result << analyzer.getNoCAverageLatency() <<endl;
  average_latency_result.close();  
  </#if>

}
