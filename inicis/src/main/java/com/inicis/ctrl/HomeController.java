package com.inicis.ctrl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.inicis.std.util.SignatureUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String pc(Locale locale, ModelMap modelMap) {
		String mid					= "INIpayTest";		                    // 상점아이디					
		modelMap.addAttribute("mid", mid);
		String signKey			    = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS";	// 웹 결제 signkey
		modelMap.addAttribute("signKey", signKey);
		try {
			String mKey = SignatureUtil.hash(signKey, "SHA-256");
			modelMap.addAttribute("mKey", mKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String timestamp			= SignatureUtil.getTimestamp();			// util에 의해서 자동생성
		modelMap.addAttribute("timestamp", timestamp);
		String orderNumber			= mid+"_"+SignatureUtil.getTimestamp();	// 가맹점 주문번호(가맹점에서 직접 설정)
		modelMap.addAttribute("orderNumber", orderNumber);
		String price				= "1000";								// 상품가격(특수기호 제외, 가맹점에서 직접 설정)
		modelMap.addAttribute("price", price);
		
		String use_chkfake			= "Y";									// verification 검증 여부 ('Y' , 'N')
		modelMap.addAttribute("use_chkfake", use_chkfake);
		
		Map<String, String> signParam = new HashMap<String, String>();

		signParam.put("oid", orderNumber);
		signParam.put("price", price);
		signParam.put("timestamp", timestamp);
		
		try {
			String signature = SignatureUtil.makeSignature(signParam);
			modelMap.addAttribute("signature", signature);
		} catch (Exception e) {
			e.printStackTrace();
		}
		signParam.put("signKey", signKey);

		try {
			String verification = SignatureUtil.makeSignature(signParam);
			modelMap.addAttribute("verification", verification);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "INIstdpay_pc_req";
	}
	
	@RequestMapping(value = "/mo", method = RequestMethod.GET)
	public String mo(@RequestParam Map<String, Object> map) {
		System.err.println(map);
		return "INImobile_mo_req";
	}
	
	@RequestMapping(value = "/INIstdpay_pc_return", method = RequestMethod.POST)
	public String INIstdpay_pc_return(@RequestParam Map<String, Object> map) {
		System.err.println(map);
		return "INIstdpay_pc_return";
	}
	
	@RequestMapping(value = "/INImobile_mo_return", method = RequestMethod.POST)
	public String INIstdpay_mo_return(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		System.err.println(map);
		System.err.println(request.getAttribute("P_REQ_URL"));
		request.setAttribute("P_REQ_URL", "https://stgmobile.inicis.com/smart/payReq.ini");
		return "INImobile_mo_return";
	}
	
}
