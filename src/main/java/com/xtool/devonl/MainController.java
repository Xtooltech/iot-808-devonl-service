package com.xtool.devonl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xtool.enterprise.RespState;
import com.xtool.enterprise.data.ComparePatterns;
import com.xtool.enterprise.data.DataSearchResult;
import com.xtool.enterprise.data.FieldCondition;
import com.xtool.iot808data.devonl.EnableDevonlMaintainer;
import com.xtool.iot808data.devonl.devonlCondition;
import com.xtool.iot808data.devonl.devonlMaintainer;
import com.xtool.iot808data.devonl.devonlModel;

@RestController
@EnableDevonlMaintainer
public class MainController {
	@Autowired
	private devonlMaintainer dataMaintainer;
	
	/**
	 * 查询：支持复杂条件和分页。
	 * @param condition 查询条件，带分页参数。
	 * @return 查询到的符合条件的数据集合。
	 */
	@RequestMapping(value="/get",method= {RequestMethod.POST})
	public RespState<DataSearchResult<devonlModel>> get(@RequestBody devonlCondition condition){
		if(dataMaintainer==null)return null;
		if(condition==null) {
			condition=new devonlCondition();
			condition.setPageIndex(1);
			condition.setPageSize(1);
			FieldCondition<String> sno=new FieldCondition<String>();
			sno.setComparePattern(ComparePatterns.EQ);
			sno.setField("sno");
			sno.setValues(new String[] {"013715129383"});
			condition.setSno(sno);
		}
		//dataMaintainer.mongoTemplate=new MongoTemplate(new MongoClient("19.87.22.3",27017), "p808");
		DataSearchResult<devonlModel> data= dataMaintainer.search(condition);
		RespState<DataSearchResult<devonlModel>> result=new RespState<DataSearchResult<devonlModel>>();
		result.setData(data);
		result.setCode(0);
		result.setMsg("");
		return result;
	}
	/**
	 * 添加或修改指定的文档。
	 * @param ignoreNull 是否忽略未设置的属性，false表示将未设置的属性值设置为null。
	 * @param model 要添加或修改的文档数据。
	 * @return 操作成功返回true，否则返回false。
	 */
	@RequestMapping(value="/devonl/upsert",method= {RequestMethod.POST})
	public RespState<Boolean> upsert(
			@RequestParam(name="ignoreNull",required=false) 
			@Nullable
			Boolean ignoreNull
			, @RequestBody
			devonlModel model){
		RespState<Boolean> result=new RespState<Boolean>();
		boolean ignull=true;
		if(ignoreNull!=null)ignull=ignoreNull;
		if(model==null || StringUtils.isEmpty(model.sno) || StringUtils.isEmpty(model.svrip)) {
			result.setCode(406);
			result.setMsg("Not acceptable");
			result.setData(false);
		}else {
			try {
				result.setData(dataMaintainer.upsert(model, ignull));
				result.setCode(0);
				result.setMsg("ok");
			}catch(Exception ex) {
				result.setData(false);
				result.setCode(500);
				result.setMsg(ex.getMessage());
			}
		}
		return result;
	}
	/**
	 * 删除指定的数据。
	 * @param sno 设备序列号。
	 * @return 操作成功返回true，否则返回false。
	 */
	@RequestMapping(value="/devonl/remove",method= {RequestMethod.POST,RequestMethod.GET})
	public RespState<Boolean> remove(
			@RequestParam(name="sno",required=true) 
			String sno){
		RespState<Boolean> result=new RespState<Boolean>();
		if(StringUtils.isEmpty(sno)) {
			result.setCode(406);
			result.setMsg("Not acceptable");
			result.setData(false);
		}else {
			try {
				result.setData(dataMaintainer.remove(sno));
				result.setCode(0);
				result.setMsg("ok");
			}catch(Exception ex) {
				result.setData(false);
				result.setCode(500);
				result.setMsg(ex.getMessage());
			}
		}
		return result;
	}

	/**
	 * 修改设备属性。
	 * @param ignoreNull 是否忽略未设置的属性，false表示将未设置的属性值设置为null。
	 * @param model 要修改的文档数据。
	 * @return 操作成功返回true，否则返回false。
	 */
	@RequestMapping(value="/devonl/refresh",method= {RequestMethod.POST,RequestMethod.GET})
	public RespState<Boolean> refresh(
			@RequestParam(name="sno",required=true) 
			String sno,
			@RequestParam(name="refdura",required=true)
			int refdura){
		RespState<Boolean> result=new RespState<Boolean>();
		if(StringUtils.isEmpty(sno)) {
			result.setCode(406);
			result.setMsg("Not acceptable");
			result.setData(false);
		}else {
			try {
				if(refdura<=0)refdura=60;
				result.setData(dataMaintainer.refresh(sno, refdura));
				result.setCode(0);
				result.setMsg("ok");
			}catch(Exception ex) {
				result.setData(false);
				result.setCode(500);
				result.setMsg(ex.getMessage());
			}
		}
		return result;
	}
    
    
    /**
     * 批量添加或修改指定的文档。
     * @param ignoreNull 是否忽略未设置的属性，false表示将未设置的属性值设置为null。
     * @param models 要添加或修改的文档数据。
     * @return 操作成功返回true，否则返回false。
     */
    @RequestMapping(value="/devonl/bupsert",method= {RequestMethod.POST})
    public RespState<Boolean> upsert(
        @RequestParam(name="ignoreNull",required=false)
        @Nullable
            Boolean ignoreNull
        , @RequestBody
              devonlModel[] models){
        RespState<Boolean> result=new RespState<Boolean>();
        boolean ignull=true;
        if(ignoreNull!=null)ignull=ignoreNull;
        if(models==null || models.length < 1) {
            result.setCode(406);
            result.setMsg("Not acceptable");
            result.setData(false);
        }else {
            try {
                result.setData(dataMaintainer.bupsert(models, ignull));
                result.setCode(0);
                result.setMsg("ok");
            }catch(Exception ex) {
                result.setData(false);
                result.setCode(500);
                result.setMsg(ex.getMessage());
            }
        }
        return result;
    }
}