package com.qzl.oa.web;

import com.qzl.oa.vo.ProcessVO;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ProcessController
 * @Description
 * @Author qzl
 * @Date 2019/10/17 16:52
 **/
@Controller
public class ProcessController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;
    @RequestMapping("/vac/start")
    public String startVacProcess(String days, String reason,HttpSession session){
        String userId = (String) session.getAttribute("user");
        //设置启动流程的人
        identityService.setAuthenticatedUserId(userId);
        //1 启动流程
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("vacationProcess");
        //2 完成用户填写的用户任务-完成第一个任务
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        //构造参数
        Map vars = new HashMap();
        vars.put("days",days);
        //请假原因
        vars.put("reason",reason);
        //声明任务-由我来做
        taskService.claim(task.getId(),userId);
        //完成
        taskService.complete(task.getId(),vars);
        return "welcome";
    }
    @RequestMapping("/vac/img")
    public String listVac(String pid,HttpSession session, Model model) {
        model.addAttribute("pid", pid);
        return "vac/img";
    }
    @RequestMapping("/vac/list")
    public String listVac(HttpSession session, Model model) {
        String userId = (String)session.getAttribute("user");

        List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery().startedBy(userId).list();

        List<ProcessVO> result = new ArrayList<ProcessVO>();
        for(ProcessInstance pi : pis) {
            String days = (String)runtimeService.getVariable(pi.getId(), "days");
            String reason = (String)runtimeService.getVariable(pi.getId(), "reason");
            System.out.println(reason + "---" + days);
            ProcessVO v = new ProcessVO();
            v.setDays(days);
            v.setReason(reason);
            v.setDate(formatDate(pi.getStartTime()));
            v.setInstanceId(pi.getId());
            result.add(v);
        }
        model.addAttribute("pis", result);
        return "vac/list";
    }

    private static String formatDate(Date d) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(d);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据流程实例id，获取流程实时跟踪图片Base64码
     * 已添加“data:image/png;base64,”
     *
     * @param processInstanceId 流程实例id
     * @return 图片base64码
     */
    @RequestMapping("/generateProcessImageBase64")
    public String generateProcessImageBase64(String processInstanceId,HttpServletResponse response) {
        OutputStream out = null;
        try {
            // 查询流程实例
            ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            // 查询流程实例
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(pi.getProcessDefinitionId()).singleResult();
            // 获取BPMN模型对象
            BpmnModel model = repositoryService.getBpmnModel(pd.getId());
            // 定义使用宋体
            String fontName = "宋体";
            // 获取流程实实例当前点的节点，需要高亮显示
            List<String> currentActs = runtimeService.getActiveActivityIds(pi.getId());
            // BPMN模型对象、图片类型、显示的节点
            InputStream is = this.processEngine
                    .getProcessEngineConfiguration()
                    .getProcessDiagramGenerator()
                    .generateDiagram(model, "png", currentActs, new ArrayList<String>(),
                            fontName, fontName, fontName,null, 1.0);

            response.setContentType("multipart/form-data;charset=utf8");
            out = response.getOutputStream();
            out.write(getImgByte(is));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
    // 将输入流转换为byte数组
    private byte[] getImgByte(InputStream is) throws IOException {
//		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
//		int b;
//		while ((b = is.read()) != -1) {
//			bytestream.write(b);
//		}
//		byte[] bs = bytestream.toByteArray();
//		bytestream.close();
//		return bs;


        BufferedInputStream bufin = new BufferedInputStream(is);
        int buffSize = 1024;
        ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

        byte[] temp = new byte[buffSize];
        int size = 0;
        while ((size = bufin.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        bufin.close();
        is.close();
        byte[] content = out.toByteArray();
        out.close();
        return content;
    }

    /**
     * 已执行flow集合
     *
     * @param bpmnModel                    模型
     * @param historicActivityInstanceList 已执行的节点，需要按照执行顺序排序，因为程序中需要判断流程最后的节点是否为结束节点
     * @return 已执行的flow
     */
    private static List<String> executedFlowIdList(BpmnModel bpmnModel, List<HistoricActivityInstance>
            historicActivityInstanceList) {
        List<String> executedFlowIdList = new ArrayList();
        int count = 0;
        if ("endEvent".equals(historicActivityInstanceList.get(historicActivityInstanceList.size() - 1)
                .getActivityType())) {
            count = historicActivityInstanceList.size();
        } else {
            count = historicActivityInstanceList.size() - 1;
        }
        for (int i = 0; i < count; i++) {
            HistoricActivityInstance hai = historicActivityInstanceList.get(i);
            FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(hai.getActivityId());
            List<SequenceFlow> sequenceFlows = flowNode.getOutgoingFlows();
            if (sequenceFlows.size() > 1) {
                HistoricActivityInstance nextHai = historicActivityInstanceList.get(i + 1);
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    if (sequenceFlow.getTargetRef().equals(nextHai.getActivityId())) {
                        executedFlowIdList.add(sequenceFlow.getId());
                    }
                }
            } else {
                if (sequenceFlows.size() != 0)
                    executedFlowIdList.add(sequenceFlows.get(0).getId());
            }
        }
        return executedFlowIdList;
    }
}
