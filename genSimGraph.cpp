#include <iostream>
#include <fstream>
#include <sstream>
#include <time.h>

#include "Util.h"
#include "TrafficAnalyzer.h"
#include "MySqlConnector.h"

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
  //--------------------------------
  	std::string dataName="SystemC";
  	MySqlConnector* connector=MySqlConnector::getInstance();
  	connector->connectMySQL();
  	connector->createDatabase(dataName);
  	time_t tt = time(NULL);
   	tm* t= localtime(&tt);
   	printf("%d-%02d-%02d %02d:%02d:%02d\n",
   	t->tm_year + 1900,
    	t->tm_mon + 1,
    	t->tm_mday,
    	t->tm_hour,
    	t->tm_min,
    	t->tm_sec);
  	stringstream timeInhalt;
  	timeInhalt<<t->tm_year + 1900<<"_"<<t->tm_mon + 1<<"_"<<t->tm_mday<<"_"<<t->tm_hour<<"_"<<t->tm_min<<"_"<<t->tm_sec;
  	std::string moduleTableName="module_simulation_";
  	std::string fastFifoRWTableName="fastfiforw_simulation_";
  	std::string fifoTableName="fifo_simulation_";
  	moduleTableName+=timeInhalt.str();
  	fastFifoRWTableName+=timeInhalt.str();
  	fifoTableName+=timeInhalt.str();
  	connector->createTable(moduleTableName);
  	connector->createTable(fastFifoRWTableName);
  	connector->createTable(fifoTableName);
  	connector->setModuleTableName(moduleTableName);
  	connector->setFastFifoRWTableName(fastFifoRWTableName);
  	connector->setFifoTableName(fifoTableName);
  	connector->startTransaction();
  	cout<<"wait  to write data to datebank"<<endl;
    //--------------------------------
  
  ProcessingElementWR* _asic1 = new ProcessingElementWR("ASIC1", 10000, 4, 8);
  _asic1->clk(clk);
  _asic1->rst(rst);
  ProcessingElementWR* _asic2 = new ProcessingElementWR("ASIC2", 20000, 4, 8);
  _asic2->clk(clk);
  _asic2->rst(rst);
  ProcessingElementWR* _asic3 = new ProcessingElementWR("ASIC3", 30000, 4, 8);
  _asic3->clk(clk);
  _asic3->rst(rst);
  ProcessingElementWR* _asic4 = new ProcessingElementWR("ASIC4", 40000, 4, 8);
  _asic4->clk(clk);
  _asic4->rst(rst);
  ProcessingElementWR* _dsp1 = new ProcessingElementWR("DSP1", 50000, 4, 8);
  _dsp1->clk(clk);
  _dsp1->rst(rst);
  ProcessingElementWR* _dsp2 = new ProcessingElementWR("DSP2", 60000, 4, 8);
  _dsp2->clk(clk);
  _dsp2->rst(rst);
  ProcessingElementWR* _dsp3 = new ProcessingElementWR("DSP3", 70000, 4, 8);
  _dsp3->clk(clk);
  _dsp3->rst(rst);
  ProcessingElementWR* _dsp4 = new ProcessingElementWR("DSP4", 80000, 4, 8);
  _dsp4->clk(clk);
  _dsp4->rst(rst);
  ProcessingElementWR* _dsp5 = new ProcessingElementWR("DSP5", 90000, 4, 8);
  _dsp5->clk(clk);
  _dsp5->rst(rst);
  ProcessingElementWR* _dsp6 = new ProcessingElementWR("DSP6", 100000, 4, 8);
  _dsp6->clk(clk);
  _dsp6->rst(rst);
  ProcessingElementWR* _dsp7 = new ProcessingElementWR("DSP7", 110000, 4, 8);
  _dsp7->clk(clk);
  _dsp7->rst(rst);
  ProcessingElementWR* _dsp8 = new ProcessingElementWR("DSP8", 120000, 4, 8);
  _dsp8->clk(clk);
  _dsp8->rst(rst);
  ProcessingElementWR* _cpu1 = new ProcessingElementWR("CPU1", 130000, 4, 8);
  _cpu1->clk(clk);
  _cpu1->rst(rst);
  ProcessingElementWR* _mem1 = new ProcessingElementWR("MEM1", 140000, 4, 8);
  _mem1->clk(clk);
  _mem1->rst(rst);
  ProcessingElementWR* _mem2 = new ProcessingElementWR("MEM2", 150000, 4, 8);
  _mem2->clk(clk);
  _mem2->rst(rst);
  ProcessingElementWR* _mem3 = new ProcessingElementWR("MEM3", 160000, 4, 8);
  _mem3->clk(clk);
  _mem3->rst(rst);
  
  RouterInfo r1_info;
  r1_info.addInputPort(1);
  r1_info.setInputChannelDeepth(0, 0, 8);
  r1_info.addOutputPort(1);
  r1_info.setOutputChannelDeepth(0, 0, 8);
  r1_info.setOutputChannelCreditNumber(0, 0, 8);
  r1_info.addOutputPort(1);
  r1_info.setOutputChannelDeepth(1, 0, 8);
  r1_info.setOutputChannelCreditNumber(1, 0, 8);
  r1_info.addInputPort(1);
  r1_info.setInputChannelDeepth(1, 0, 8);
  r1_info.addOutputPort(1);
  r1_info.setOutputChannelDeepth(2, 0, 8);
  r1_info.setOutputChannelCreditNumber(2, 0, 8);
  r1_info.addInputPort(1);
  r1_info.setInputChannelDeepth(2, 0, 8);
	
  RouterInfo r2_info;
  r2_info.addInputPort(1);
  r2_info.setInputChannelDeepth(0, 0, 8);
  r2_info.addOutputPort(1);
  r2_info.setOutputChannelDeepth(0, 0, 8);
  r2_info.setOutputChannelCreditNumber(0, 0, 8);
  r2_info.addInputPort(1);
  r2_info.setInputChannelDeepth(1, 0, 8);
  r2_info.addOutputPort(1);
  r2_info.setOutputChannelDeepth(1, 0, 8);
  r2_info.setOutputChannelCreditNumber(1, 0, 8);
  r2_info.addOutputPort(1);
  r2_info.setOutputChannelDeepth(2, 0, 8);
  r2_info.setOutputChannelCreditNumber(2, 0, 8);
  r2_info.addInputPort(1);
  r2_info.setInputChannelDeepth(2, 0, 8);
  r2_info.addOutputPort(1);
  r2_info.setOutputChannelDeepth(3, 0, 8);
  r2_info.setOutputChannelCreditNumber(3, 0, 8);
  r2_info.addInputPort(1);
  r2_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r3_info;
  r3_info.addInputPort(1);
  r3_info.setInputChannelDeepth(0, 0, 8);
  r3_info.addOutputPort(1);
  r3_info.setOutputChannelDeepth(0, 0, 8);
  r3_info.setOutputChannelCreditNumber(0, 0, 8);
  r3_info.addInputPort(1);
  r3_info.setInputChannelDeepth(1, 0, 8);
  r3_info.addOutputPort(1);
  r3_info.setOutputChannelDeepth(1, 0, 8);
  r3_info.setOutputChannelCreditNumber(1, 0, 8);
  r3_info.addOutputPort(1);
  r3_info.setOutputChannelDeepth(2, 0, 8);
  r3_info.setOutputChannelCreditNumber(2, 0, 8);
  r3_info.addInputPort(1);
  r3_info.setInputChannelDeepth(2, 0, 8);
  r3_info.addOutputPort(1);
  r3_info.setOutputChannelDeepth(3, 0, 8);
  r3_info.setOutputChannelCreditNumber(3, 0, 8);
  r3_info.addInputPort(1);
  r3_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r4_info;
  r4_info.addInputPort(1);
  r4_info.setInputChannelDeepth(0, 0, 8);
  r4_info.addOutputPort(1);
  r4_info.setOutputChannelDeepth(0, 0, 8);
  r4_info.setOutputChannelCreditNumber(0, 0, 8);
  r4_info.addInputPort(1);
  r4_info.setInputChannelDeepth(1, 0, 8);
  r4_info.addOutputPort(1);
  r4_info.setOutputChannelDeepth(1, 0, 8);
  r4_info.setOutputChannelCreditNumber(1, 0, 8);
  r4_info.addOutputPort(1);
  r4_info.setOutputChannelDeepth(2, 0, 8);
  r4_info.setOutputChannelCreditNumber(2, 0, 8);
  r4_info.addInputPort(1);
  r4_info.setInputChannelDeepth(2, 0, 8);
	
  RouterInfo r5_info;
  r5_info.addInputPort(1);
  r5_info.setInputChannelDeepth(0, 0, 8);
  r5_info.addOutputPort(1);
  r5_info.setOutputChannelDeepth(0, 0, 8);
  r5_info.setOutputChannelCreditNumber(0, 0, 8);
  r5_info.addInputPort(1);
  r5_info.setInputChannelDeepth(1, 0, 8);
  r5_info.addOutputPort(1);
  r5_info.setOutputChannelDeepth(1, 0, 8);
  r5_info.setOutputChannelCreditNumber(1, 0, 8);
  r5_info.addOutputPort(1);
  r5_info.setOutputChannelDeepth(2, 0, 8);
  r5_info.setOutputChannelCreditNumber(2, 0, 8);
  r5_info.addInputPort(1);
  r5_info.setInputChannelDeepth(2, 0, 8);
  r5_info.addOutputPort(1);
  r5_info.setOutputChannelDeepth(3, 0, 8);
  r5_info.setOutputChannelCreditNumber(3, 0, 8);
  r5_info.addInputPort(1);
  r5_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r6_info;
  r6_info.addInputPort(1);
  r6_info.setInputChannelDeepth(0, 0, 8);
  r6_info.addOutputPort(1);
  r6_info.setOutputChannelDeepth(0, 0, 8);
  r6_info.setOutputChannelCreditNumber(0, 0, 8);
  r6_info.addInputPort(1);
  r6_info.setInputChannelDeepth(1, 0, 8);
  r6_info.addOutputPort(1);
  r6_info.setOutputChannelDeepth(1, 0, 8);
  r6_info.setOutputChannelCreditNumber(1, 0, 8);
  r6_info.addInputPort(1);
  r6_info.setInputChannelDeepth(2, 0, 8);
  r6_info.addOutputPort(1);
  r6_info.setOutputChannelDeepth(2, 0, 8);
  r6_info.setOutputChannelCreditNumber(2, 0, 8);
  r6_info.addOutputPort(1);
  r6_info.setOutputChannelDeepth(3, 0, 8);
  r6_info.setOutputChannelCreditNumber(3, 0, 8);
  r6_info.addInputPort(1);
  r6_info.setInputChannelDeepth(3, 0, 8);
  r6_info.addOutputPort(1);
  r6_info.setOutputChannelDeepth(4, 0, 8);
  r6_info.setOutputChannelCreditNumber(4, 0, 8);
  r6_info.addInputPort(1);
  r6_info.setInputChannelDeepth(4, 0, 8);
	
  RouterInfo r7_info;
  r7_info.addInputPort(1);
  r7_info.setInputChannelDeepth(0, 0, 8);
  r7_info.addOutputPort(1);
  r7_info.setOutputChannelDeepth(0, 0, 8);
  r7_info.setOutputChannelCreditNumber(0, 0, 8);
  r7_info.addInputPort(1);
  r7_info.setInputChannelDeepth(1, 0, 8);
  r7_info.addOutputPort(1);
  r7_info.setOutputChannelDeepth(1, 0, 8);
  r7_info.setOutputChannelCreditNumber(1, 0, 8);
  r7_info.addInputPort(1);
  r7_info.setInputChannelDeepth(2, 0, 8);
  r7_info.addOutputPort(1);
  r7_info.setOutputChannelDeepth(2, 0, 8);
  r7_info.setOutputChannelCreditNumber(2, 0, 8);
  r7_info.addOutputPort(1);
  r7_info.setOutputChannelDeepth(3, 0, 8);
  r7_info.setOutputChannelCreditNumber(3, 0, 8);
  r7_info.addInputPort(1);
  r7_info.setInputChannelDeepth(3, 0, 8);
  r7_info.addOutputPort(1);
  r7_info.setOutputChannelDeepth(4, 0, 8);
  r7_info.setOutputChannelCreditNumber(4, 0, 8);
  r7_info.addInputPort(1);
  r7_info.setInputChannelDeepth(4, 0, 8);
	
  RouterInfo r8_info;
  r8_info.addInputPort(1);
  r8_info.setInputChannelDeepth(0, 0, 8);
  r8_info.addOutputPort(1);
  r8_info.setOutputChannelDeepth(0, 0, 8);
  r8_info.setOutputChannelCreditNumber(0, 0, 8);
  r8_info.addInputPort(1);
  r8_info.setInputChannelDeepth(1, 0, 8);
  r8_info.addOutputPort(1);
  r8_info.setOutputChannelDeepth(1, 0, 8);
  r8_info.setOutputChannelCreditNumber(1, 0, 8);
  r8_info.addInputPort(1);
  r8_info.setInputChannelDeepth(2, 0, 8);
  r8_info.addOutputPort(1);
  r8_info.setOutputChannelDeepth(2, 0, 8);
  r8_info.setOutputChannelCreditNumber(2, 0, 8);
  r8_info.addOutputPort(1);
  r8_info.setOutputChannelDeepth(3, 0, 8);
  r8_info.setOutputChannelCreditNumber(3, 0, 8);
  r8_info.addInputPort(1);
  r8_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r9_info;
  r9_info.addInputPort(1);
  r9_info.setInputChannelDeepth(0, 0, 8);
  r9_info.addOutputPort(1);
  r9_info.setOutputChannelDeepth(0, 0, 8);
  r9_info.setOutputChannelCreditNumber(0, 0, 8);
  r9_info.addInputPort(1);
  r9_info.setInputChannelDeepth(1, 0, 8);
  r9_info.addOutputPort(1);
  r9_info.setOutputChannelDeepth(1, 0, 8);
  r9_info.setOutputChannelCreditNumber(1, 0, 8);
  r9_info.addOutputPort(1);
  r9_info.setOutputChannelDeepth(2, 0, 8);
  r9_info.setOutputChannelCreditNumber(2, 0, 8);
  r9_info.addInputPort(1);
  r9_info.setInputChannelDeepth(2, 0, 8);
  r9_info.addOutputPort(1);
  r9_info.setOutputChannelDeepth(3, 0, 8);
  r9_info.setOutputChannelCreditNumber(3, 0, 8);
  r9_info.addInputPort(1);
  r9_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r10_info;
  r10_info.addInputPort(1);
  r10_info.setInputChannelDeepth(0, 0, 8);
  r10_info.addOutputPort(1);
  r10_info.setOutputChannelDeepth(0, 0, 8);
  r10_info.setOutputChannelCreditNumber(0, 0, 8);
  r10_info.addInputPort(1);
  r10_info.setInputChannelDeepth(1, 0, 8);
  r10_info.addOutputPort(1);
  r10_info.setOutputChannelDeepth(1, 0, 8);
  r10_info.setOutputChannelCreditNumber(1, 0, 8);
  r10_info.addInputPort(1);
  r10_info.setInputChannelDeepth(2, 0, 8);
  r10_info.addOutputPort(1);
  r10_info.setOutputChannelDeepth(2, 0, 8);
  r10_info.setOutputChannelCreditNumber(2, 0, 8);
  r10_info.addOutputPort(1);
  r10_info.setOutputChannelDeepth(3, 0, 8);
  r10_info.setOutputChannelCreditNumber(3, 0, 8);
  r10_info.addInputPort(1);
  r10_info.setInputChannelDeepth(3, 0, 8);
  r10_info.addOutputPort(1);
  r10_info.setOutputChannelDeepth(4, 0, 8);
  r10_info.setOutputChannelCreditNumber(4, 0, 8);
  r10_info.addInputPort(1);
  r10_info.setInputChannelDeepth(4, 0, 8);
	
  RouterInfo r11_info;
  r11_info.addInputPort(1);
  r11_info.setInputChannelDeepth(0, 0, 8);
  r11_info.addOutputPort(1);
  r11_info.setOutputChannelDeepth(0, 0, 8);
  r11_info.setOutputChannelCreditNumber(0, 0, 8);
  r11_info.addInputPort(1);
  r11_info.setInputChannelDeepth(1, 0, 8);
  r11_info.addOutputPort(1);
  r11_info.setOutputChannelDeepth(1, 0, 8);
  r11_info.setOutputChannelCreditNumber(1, 0, 8);
  r11_info.addInputPort(1);
  r11_info.setInputChannelDeepth(2, 0, 8);
  r11_info.addOutputPort(1);
  r11_info.setOutputChannelDeepth(2, 0, 8);
  r11_info.setOutputChannelCreditNumber(2, 0, 8);
  r11_info.addOutputPort(1);
  r11_info.setOutputChannelDeepth(3, 0, 8);
  r11_info.setOutputChannelCreditNumber(3, 0, 8);
  r11_info.addInputPort(1);
  r11_info.setInputChannelDeepth(3, 0, 8);
  r11_info.addOutputPort(1);
  r11_info.setOutputChannelDeepth(4, 0, 8);
  r11_info.setOutputChannelCreditNumber(4, 0, 8);
  r11_info.addInputPort(1);
  r11_info.setInputChannelDeepth(4, 0, 8);
	
  RouterInfo r12_info;
  r12_info.addInputPort(1);
  r12_info.setInputChannelDeepth(0, 0, 8);
  r12_info.addOutputPort(1);
  r12_info.setOutputChannelDeepth(0, 0, 8);
  r12_info.setOutputChannelCreditNumber(0, 0, 8);
  r12_info.addInputPort(1);
  r12_info.setInputChannelDeepth(1, 0, 8);
  r12_info.addOutputPort(1);
  r12_info.setOutputChannelDeepth(1, 0, 8);
  r12_info.setOutputChannelCreditNumber(1, 0, 8);
  r12_info.addInputPort(1);
  r12_info.setInputChannelDeepth(2, 0, 8);
  r12_info.addOutputPort(1);
  r12_info.setOutputChannelDeepth(2, 0, 8);
  r12_info.setOutputChannelCreditNumber(2, 0, 8);
  r12_info.addOutputPort(1);
  r12_info.setOutputChannelDeepth(3, 0, 8);
  r12_info.setOutputChannelCreditNumber(3, 0, 8);
  r12_info.addInputPort(1);
  r12_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r13_info;
  r13_info.addInputPort(1);
  r13_info.setInputChannelDeepth(0, 0, 8);
  r13_info.addOutputPort(1);
  r13_info.setOutputChannelDeepth(0, 0, 8);
  r13_info.setOutputChannelCreditNumber(0, 0, 8);
  r13_info.addInputPort(1);
  r13_info.setInputChannelDeepth(1, 0, 8);
  r13_info.addOutputPort(1);
  r13_info.setOutputChannelDeepth(1, 0, 8);
  r13_info.setOutputChannelCreditNumber(1, 0, 8);
  r13_info.addOutputPort(1);
  r13_info.setOutputChannelDeepth(2, 0, 8);
  r13_info.setOutputChannelCreditNumber(2, 0, 8);
  r13_info.addInputPort(1);
  r13_info.setInputChannelDeepth(2, 0, 8);
	
  RouterInfo r14_info;
  r14_info.addInputPort(1);
  r14_info.setInputChannelDeepth(0, 0, 8);
  r14_info.addOutputPort(1);
  r14_info.setOutputChannelDeepth(0, 0, 8);
  r14_info.setOutputChannelCreditNumber(0, 0, 8);
  r14_info.addInputPort(1);
  r14_info.setInputChannelDeepth(1, 0, 8);
  r14_info.addOutputPort(1);
  r14_info.setOutputChannelDeepth(1, 0, 8);
  r14_info.setOutputChannelCreditNumber(1, 0, 8);
  r14_info.addInputPort(1);
  r14_info.setInputChannelDeepth(2, 0, 8);
  r14_info.addOutputPort(1);
  r14_info.setOutputChannelDeepth(2, 0, 8);
  r14_info.setOutputChannelCreditNumber(2, 0, 8);
  r14_info.addOutputPort(1);
  r14_info.setOutputChannelDeepth(3, 0, 8);
  r14_info.setOutputChannelCreditNumber(3, 0, 8);
  r14_info.addInputPort(1);
  r14_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r15_info;
  r15_info.addInputPort(1);
  r15_info.setInputChannelDeepth(0, 0, 8);
  r15_info.addOutputPort(1);
  r15_info.setOutputChannelDeepth(0, 0, 8);
  r15_info.setOutputChannelCreditNumber(0, 0, 8);
  r15_info.addInputPort(1);
  r15_info.setInputChannelDeepth(1, 0, 8);
  r15_info.addOutputPort(1);
  r15_info.setOutputChannelDeepth(1, 0, 8);
  r15_info.setOutputChannelCreditNumber(1, 0, 8);
  r15_info.addInputPort(1);
  r15_info.setInputChannelDeepth(2, 0, 8);
  r15_info.addOutputPort(1);
  r15_info.setOutputChannelDeepth(2, 0, 8);
  r15_info.setOutputChannelCreditNumber(2, 0, 8);
  r15_info.addOutputPort(1);
  r15_info.setOutputChannelDeepth(3, 0, 8);
  r15_info.setOutputChannelCreditNumber(3, 0, 8);
  r15_info.addInputPort(1);
  r15_info.setInputChannelDeepth(3, 0, 8);
	
  RouterInfo r16_info;
  r16_info.addInputPort(1);
  r16_info.setInputChannelDeepth(0, 0, 8);
  r16_info.addOutputPort(1);
  r16_info.setOutputChannelDeepth(0, 0, 8);
  r16_info.setOutputChannelCreditNumber(0, 0, 8);
  r16_info.addInputPort(1);
  r16_info.setInputChannelDeepth(1, 0, 8);
  r16_info.addOutputPort(1);
  r16_info.setOutputChannelDeepth(1, 0, 8);
  r16_info.setOutputChannelCreditNumber(1, 0, 8);
  r16_info.addInputPort(1);
  r16_info.setInputChannelDeepth(2, 0, 8);
  r16_info.addOutputPort(1);
  r16_info.setOutputChannelDeepth(2, 0, 8);
  r16_info.setOutputChannelCreditNumber(2, 0, 8);
	
  _asic4->setDataFlowLoad(0, 197*load_scale_factor); 
  _asic4->setDataFlowLoad(1, 33848*load_scale_factor); 
  _dsp1->setDataFlowLoad(2, 20363*load_scale_factor); 
  _dsp1->setDataFlowLoad(3, 33848*load_scale_factor); 
  _dsp1->setDataFlowLoad(4, 33848*load_scale_factor); 
  _dsp2->setDataFlowLoad(5, 20363*load_scale_factor); 
  _dsp3->setDataFlowLoad(6, 7061*load_scale_factor); 
  _dsp3->setDataFlowLoad(7, 7061*load_scale_factor); 
  _dsp3->setDataFlowLoad(8, 38016*load_scale_factor); 
  _asic2->setDataFlowLoad(9, 25*load_scale_factor); 
  _asic2->setDataFlowLoad(10, 764*load_scale_factor); 
  _asic2->setDataFlowLoad(11, 640*load_scale_factor); 
  _cpu1->setDataFlowLoad(12, 38016*load_scale_factor); 
  _cpu1->setDataFlowLoad(13, 38016*load_scale_factor); 
  _mem1->setDataFlowLoad(14, 197*load_scale_factor); 
  _mem1->setDataFlowLoad(15, 75250*load_scale_factor); 
  _dsp5->setDataFlowLoad(16, 2692*load_scale_factor); 
  _dsp6->setDataFlowLoad(17, 28248*load_scale_factor); 
  _asic1->setDataFlowLoad(18, 80*load_scale_factor); 
  _asic1->setDataFlowLoad(19, 25*load_scale_factor); 
  _mem2->setDataFlowLoad(20, 7065*load_scale_factor); 
  _asic3->setDataFlowLoad(21, 144*load_scale_factor); 
  _asic3->setDataFlowLoad(22, 641*load_scale_factor); 
  _dsp4->setDataFlowLoad(23, 116873*load_scale_factor); 
  _dsp4->setDataFlowLoad(24, 33848*load_scale_factor); 
  _mem3->setDataFlowLoad(25, 75584*load_scale_factor); 
  _dsp8->setDataFlowLoad(26, 80*load_scale_factor); 
  _dsp8->setDataFlowLoad(27, 28265*load_scale_factor); 
  _dsp7->setDataFlowLoad(28, 7065*load_scale_factor); 

  r1_info.addRouteEntry(0, 0, 0, 1, 0);
  r2_info.addRouteEntry(0, 1, 0, 3, 0);
  r6_info.addRouteEntry(0, 1, 0, 0, 0);
  
  r1_info.addRouteEntry(1, 0, 0, 1, 0);
  r2_info.addRouteEntry(1, 1, 0, 0, 0);
  
  r2_info.addRouteEntry(2, 0, 0, 3, 0);
  r6_info.addRouteEntry(2, 1, 0, 0, 0);
  
  r2_info.addRouteEntry(3, 0, 0, 2, 0);
  r3_info.addRouteEntry(3, 1, 0, 0, 0);
  
  r2_info.addRouteEntry(4, 0, 0, 1, 0);
  r1_info.addRouteEntry(4, 1, 0, 2, 0);
  r5_info.addRouteEntry(4, 1, 0, 0, 0);
  
  r3_info.addRouteEntry(5, 0, 0, 1, 0);
  r2_info.addRouteEntry(5, 2, 0, 0, 0);
  
  r4_info.addRouteEntry(6, 0, 0, 2, 0);
  r8_info.addRouteEntry(6, 1, 0, 0, 0);
  
  r4_info.addRouteEntry(7, 0, 0, 1, 0);
  r3_info.addRouteEntry(7, 2, 0, 1, 0);
  r2_info.addRouteEntry(7, 2, 0, 1, 0);
  r1_info.addRouteEntry(7, 1, 0, 2, 0);
  r5_info.addRouteEntry(7, 1, 0, 3, 0);
  r9_info.addRouteEntry(7, 1, 0, 0, 0);
  
  r4_info.addRouteEntry(8, 0, 0, 1, 0);
  r3_info.addRouteEntry(8, 2, 0, 1, 0);
  r2_info.addRouteEntry(8, 2, 0, 1, 0);
  r1_info.addRouteEntry(8, 1, 0, 0, 0);
  
  r5_info.addRouteEntry(9, 0, 0, 2, 0);
  r6_info.addRouteEntry(9, 2, 0, 4, 0);
  r10_info.addRouteEntry(9, 1, 0, 0, 0);
  
  r5_info.addRouteEntry(10, 0, 0, 2, 0);
  r6_info.addRouteEntry(10, 2, 0, 3, 0);
  r7_info.addRouteEntry(10, 2, 0, 3, 0);
  r8_info.addRouteEntry(10, 2, 0, 3, 0);
  r12_info.addRouteEntry(10, 1, 0, 0, 0);
  
  r5_info.addRouteEntry(11, 0, 0, 2, 0);
  r6_info.addRouteEntry(11, 2, 0, 3, 0);
  r7_info.addRouteEntry(11, 2, 0, 4, 0);
  r11_info.addRouteEntry(11, 1, 0, 0, 0);
  
  r6_info.addRouteEntry(12, 0, 0, 3, 0);
  r7_info.addRouteEntry(12, 2, 0, 0, 0);
  
  r6_info.addRouteEntry(13, 0, 0, 4, 0);
  r10_info.addRouteEntry(13, 1, 0, 4, 0);
  r14_info.addRouteEntry(13, 1, 0, 0, 0);
  
  r7_info.addRouteEntry(14, 0, 0, 2, 0);
  r6_info.addRouteEntry(14, 3, 0, 2, 0);
  r5_info.addRouteEntry(14, 2, 0, 1, 0);
  r1_info.addRouteEntry(14, 2, 0, 0, 0);
  
  r7_info.addRouteEntry(15, 0, 0, 2, 0);
  r6_info.addRouteEntry(15, 3, 0, 0, 0);
  
  r8_info.addRouteEntry(16, 0, 0, 2, 0);
  r7_info.addRouteEntry(16, 3, 0, 2, 0);
  r6_info.addRouteEntry(16, 3, 0, 2, 0);
  r5_info.addRouteEntry(16, 2, 0, 3, 0);
  r9_info.addRouteEntry(16, 1, 0, 0, 0);
  
  r9_info.addRouteEntry(17, 0, 0, 1, 0);
  r5_info.addRouteEntry(17, 3, 0, 0, 0);
  
  r10_info.addRouteEntry(18, 0, 0, 2, 0);
  r9_info.addRouteEntry(18, 2, 0, 1, 0);
  r5_info.addRouteEntry(18, 3, 0, 0, 0);
  
  r10_info.addRouteEntry(19, 0, 0, 3, 0);
  r11_info.addRouteEntry(19, 2, 0, 4, 0);
  r15_info.addRouteEntry(19, 1, 0, 0, 0);
  
  r11_info.addRouteEntry(20, 0, 0, 3, 0);
  r12_info.addRouteEntry(20, 2, 0, 0, 0);
  
  r12_info.addRouteEntry(21, 0, 0, 2, 0);
  r11_info.addRouteEntry(21, 3, 0, 2, 0);
  r10_info.addRouteEntry(21, 3, 0, 2, 0);
  r9_info.addRouteEntry(21, 2, 0, 3, 0);
  r13_info.addRouteEntry(21, 1, 0, 0, 0);
  
  r12_info.addRouteEntry(22, 0, 0, 2, 0);
  r11_info.addRouteEntry(22, 3, 0, 4, 0);
  r15_info.addRouteEntry(22, 1, 0, 0, 0);
  
  r13_info.addRouteEntry(23, 0, 0, 2, 0);
  r14_info.addRouteEntry(23, 2, 0, 1, 0);
  r10_info.addRouteEntry(23, 4, 0, 1, 0);
  r6_info.addRouteEntry(23, 4, 0, 0, 0);
  
  r13_info.addRouteEntry(24, 0, 0, 2, 0);
  r14_info.addRouteEntry(24, 2, 0, 3, 0);
  r15_info.addRouteEntry(24, 2, 0, 1, 0);
  r11_info.addRouteEntry(24, 4, 0, 1, 0);
  r7_info.addRouteEntry(24, 4, 0, 1, 0);
  r3_info.addRouteEntry(24, 3, 0, 0, 0);
  
  r14_info.addRouteEntry(25, 0, 0, 1, 0);
  r10_info.addRouteEntry(25, 4, 0, 1, 0);
  r6_info.addRouteEntry(25, 4, 0, 0, 0);
  
  r15_info.addRouteEntry(26, 0, 0, 2, 0);
  r14_info.addRouteEntry(26, 3, 0, 1, 0);
  r10_info.addRouteEntry(26, 4, 0, 0, 0);
  
  r15_info.addRouteEntry(27, 0, 0, 3, 0);
  r16_info.addRouteEntry(27, 2, 0, 0, 0);
  
  r16_info.addRouteEntry(28, 0, 0, 2, 0);
  r15_info.addRouteEntry(28, 3, 0, 1, 0);
  r11_info.addRouteEntry(28, 4, 0, 0, 0);
  
  Router* r1 = new Router("R1", r1_info);
  r1->clk(clk); 
  r1->rst(rst);
  Router* r2 = new Router("R2", r2_info);
  r2->clk(clk); 
  r2->rst(rst);
  Router* r3 = new Router("R3", r3_info);
  r3->clk(clk); 
  r3->rst(rst);
  Router* r4 = new Router("R4", r4_info);
  r4->clk(clk); 
  r4->rst(rst);
  Router* r5 = new Router("R5", r5_info);
  r5->clk(clk); 
  r5->rst(rst);
  Router* r6 = new Router("R6", r6_info);
  r6->clk(clk); 
  r6->rst(rst);
  Router* r7 = new Router("R7", r7_info);
  r7->clk(clk); 
  r7->rst(rst);
  Router* r8 = new Router("R8", r8_info);
  r8->clk(clk); 
  r8->rst(rst);
  Router* r9 = new Router("R9", r9_info);
  r9->clk(clk); 
  r9->rst(rst);
  Router* r10 = new Router("R10", r10_info);
  r10->clk(clk); 
  r10->rst(rst);
  Router* r11 = new Router("R11", r11_info);
  r11->clk(clk); 
  r11->rst(rst);
  Router* r12 = new Router("R12", r12_info);
  r12->clk(clk); 
  r12->rst(rst);
  Router* r13 = new Router("R13", r13_info);
  r13->clk(clk); 
  r13->rst(rst);
  Router* r14 = new Router("R14", r14_info);
  r14->clk(clk); 
  r14->rst(rst);
  Router* r15 = new Router("R15", r15_info);
  r15->clk(clk); 
  r15->rst(rst);
  Router* r16 = new Router("R16", r16_info);
  r16->clk(clk); 
  r16->rst(rst);

  connectPeWROutportToRouter(_asic4, r1, 0);
  connectPeWRInportToRouter(_asic4, r1, 0);
  connectPeWROutportToRouter(_dsp1, r2, 0);
  connectPeWRInportToRouter(_dsp1, r2, 0);
  connectPeWROutportToRouter(_dsp2, r3, 0);
  connectPeWRInportToRouter(_dsp2, r3, 0);
  connectPeWROutportToRouter(_dsp3, r4, 0);
  connectPeWRInportToRouter(_dsp3, r4, 0);
  connectPeWROutportToRouter(_asic2, r5, 0);
  connectPeWRInportToRouter(_asic2, r5, 0);
  connectPeWROutportToRouter(_cpu1, r6, 0);
  connectPeWRInportToRouter(_cpu1, r6, 0);
  connectPeWROutportToRouter(_mem1, r7, 0);
  connectPeWRInportToRouter(_mem1, r7, 0);
  connectPeWROutportToRouter(_dsp5, r8, 0);
  connectPeWRInportToRouter(_dsp5, r8, 0);
  connectPeWROutportToRouter(_dsp6, r9, 0);
  connectPeWRInportToRouter(_dsp6, r9, 0);
  connectPeWROutportToRouter(_asic1, r10, 0);
  connectPeWRInportToRouter(_asic1, r10, 0);
  connectPeWROutportToRouter(_mem2, r11, 0);
  connectPeWRInportToRouter(_mem2, r11, 0);
  connectPeWROutportToRouter(_asic3, r12, 0);
  connectPeWRInportToRouter(_asic3, r12, 0);
  connectPeWROutportToRouter(_dsp4, r13, 0);
  connectPeWRInportToRouter(_dsp4, r13, 0);
  connectPeWROutportToRouter(_mem3, r14, 0);
  connectPeWRInportToRouter(_mem3, r14, 0);
  connectPeWROutportToRouter(_dsp8, r15, 0);
  connectPeWRInportToRouter(_dsp8, r15, 0);
  connectPeWROutportToRouter(_dsp7, r16, 0);
  connectPeWRInportToRouter(_dsp7, r16, 0);
  connectRouters(r1, 1, r2, 1);
  connectRouters(r2, 1, r1, 1);
  connectRouters(r2, 2, r3, 1);
  connectRouters(r3, 1, r2, 2);
  connectRouters(r3, 2, r4, 1);
  connectRouters(r4, 1, r3, 2);
  connectRouters(r1, 2, r5, 1);
  connectRouters(r5, 1, r1, 2);
  connectRouters(r2, 3, r6, 1);
  connectRouters(r6, 1, r2, 3);
  connectRouters(r3, 3, r7, 1);
  connectRouters(r7, 1, r3, 3);
  connectRouters(r4, 2, r8, 1);
  connectRouters(r8, 1, r4, 2);
  connectRouters(r5, 2, r6, 2);
  connectRouters(r6, 2, r5, 2);
  connectRouters(r6, 3, r7, 2);
  connectRouters(r7, 2, r6, 3);
  connectRouters(r7, 3, r8, 2);
  connectRouters(r8, 2, r7, 3);
  connectRouters(r5, 3, r9, 1);
  connectRouters(r9, 1, r5, 3);
  connectRouters(r6, 4, r10, 1);
  connectRouters(r10, 1, r6, 4);
  connectRouters(r7, 4, r11, 1);
  connectRouters(r11, 1, r7, 4);
  connectRouters(r8, 3, r12, 1);
  connectRouters(r12, 1, r8, 3);
  connectRouters(r9, 2, r10, 2);
  connectRouters(r10, 2, r9, 2);
  connectRouters(r10, 3, r11, 2);
  connectRouters(r11, 2, r10, 3);
  connectRouters(r11, 3, r12, 2);
  connectRouters(r12, 2, r11, 3);
  connectRouters(r9, 3, r13, 1);
  connectRouters(r13, 1, r9, 3);
  connectRouters(r10, 4, r14, 1);
  connectRouters(r14, 1, r10, 4);
  connectRouters(r11, 4, r15, 1);
  connectRouters(r15, 1, r11, 4);
  connectRouters(r12, 3, r16, 1);
  connectRouters(r16, 1, r12, 3);
  connectRouters(r13, 2, r14, 2);
  connectRouters(r14, 2, r13, 2);
  connectRouters(r14, 3, r15, 2);
  connectRouters(r15, 2, r14, 3);
  connectRouters(r15, 3, r16, 2);
  connectRouters(r16, 2, r15, 3);
          
  rst.write(true);
  sc_start(100, SC_NS );
  rst.write(false);
  sc_start(1000, SC_NS );
  
 cout<<"simulation finish !"<<endl;
 	connector->commitTransaction();
 cout<<"finish writing data  to datebank"<<endl;
  
  TrafficAnalyzer analyzer;
  analyzer.addProducerLogFileName("log_files/ASIC1.producer.txt");
  analyzer.addProducerLogFileName("log_files/ASIC2.producer.txt");
  analyzer.addProducerLogFileName("log_files/ASIC3.producer.txt");
  analyzer.addProducerLogFileName("log_files/ASIC4.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP1.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP2.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP3.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP4.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP5.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP6.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP7.producer.txt");
  analyzer.addProducerLogFileName("log_files/DSP8.producer.txt");
  analyzer.addProducerLogFileName("log_files/CPU1.producer.txt");
  analyzer.addProducerLogFileName("log_files/MEM1.producer.txt");
  analyzer.addProducerLogFileName("log_files/MEM2.producer.txt");
  analyzer.addProducerLogFileName("log_files/MEM3.producer.txt");
  
  analyzer.addConsumerLogFileName("log_files/ASIC1.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/ASIC2.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/ASIC3.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/ASIC4.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP1.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP2.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP3.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP4.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP5.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP6.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP7.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/DSP8.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/CPU1.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/MEM1.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/MEM2.consumer.txt");
  analyzer.addConsumerLogFileName("log_files/MEM3.consumer.txt");
  
  analyzer.readFiles();
  // analyzer.analyzeOneFlow(2);
  analyzer.analyzeAllFlows();
  analyzer.reportAllFlows();
  
  ofstream average_latency_result;
  average_latency_result.open(logFileName, std::ofstream::out | std::ofstream::app);
  average_latency_result << analyzer.getNoCAverageLatency() <<endl;
  average_latency_result.close();  

}
