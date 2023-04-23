package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@SuppressWarnings("all")
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model){
        Result<Map<String,Object>> result = listFeignClient.list(searchParam);

        model.addAllAttributes(result.getData());

        //设置搜索条件回显
        model.addAttribute("searchParam",searchParam);

        //拼接urlParam
        String urlParam=this.makeUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);

        return "list/index";
    }

    //拼接路径
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder builder = new StringBuilder();
        //入口1：拼接关键字
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            builder.append("keyword=").append(searchParam.getKeyword());
        }
        //入口2：拼接分类
        if(searchParam.getCategory1Id()!=null){
            builder.append("category1Id=").append(searchParam.getCategory1Id());
        }
        if(searchParam.getCategory2Id()!=null){
            builder.append("category2Id=").append(searchParam.getCategory2Id());
        }
        if(searchParam.getCategory3Id()!=null){
            builder.append("category3Id=").append(searchParam.getCategory3Id());
        }
        //品牌
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            //处理  2:华为
            if(builder.length()>0){
                builder.append("&trademark=").append(searchParam.getTrademark());
            }
        }
        //平台属性
        String[] props = searchParam.getProps();
        //判断
        if(props!=null&&props.length>0){
            for (String prop : props) {
                if(builder.length()>0){
                    builder.append("&props=").append(prop);
                }
            }
        }


        return "list.html?"+builder.toString();
    }
}
