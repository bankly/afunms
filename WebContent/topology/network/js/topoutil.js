//��ps���Լ�����imgͼƬ
var XHR = new function(){
	this.getInstance = function(){
		var http;
		try{
			http = new ActiveXObject("Microsoft.XMLHTTP")
		}catch(e){
			try{
				http = new XMLHttpRequest();
			}catch(e){
				alert("Error!�޷�����XHR����");
			}
		}
		return http;
	}
	this.getInstanceBy = function(method,url,isAsyn){
		var http;
		try{
			http = new ActiveXObject("Microsoft.XMLHTTP")
		}catch(e){
			try{
				http = new XMLHttpRequest();
			}catch(e){
				alert("Error!�޷�����XHR����");
			}
		}
		http.open(method,url,isAsyn);
		return http;
	}
};
/**
 * ��ʾģ̬���� ����������ֵ
 * @param url
 * @param arguments
 * @param features
 */
function showModalDialogAndDo(url,arguments,features){
	var returnValue = window.showModalDialog(url,arguments,features);
	if(returnValue){
		window.location.reload();
	}
}
function setImage(img,ps){
	if(isFF){
		if(ps.x){
			img.setAttribute('x',ps.x);
		}
		if(ps.y){
			img.setAttribute('y',ps.y);
		}
		if(ps.w){
			img.setAttribute('width',ps.w);
		}
		if(ps.h){
			img.setAttribute('height',ps.h);
		}
		if(ps.src){
			img.setAttributeNS('http://www.w3.org/1999/xlink','xlink:href',ps.src);
		}
	}else{
		if(ps.x){
			img.style.left = ps.x;
		}
		if(ps.y){
			img.style.top = ps.y;	
		}
		if(ps.w){
			img.style.width = ps.w;
		}
		if(ps.h){
			img.style.height = ps.h;
		}
		if(ps.src){
			img.src = ps.src;
		}
	}
}
function getImagePropertiesBy(img, ps){
	if(isFF){
		if(ps.x){
			ps.x = img.getAttribute('x');
		}
		if(ps.y){
			ps.y = img.getAttribute('y');
		}
		if(ps.w){
			ps.w = img.getAttribute('width');
		}
		if(ps.h){
			ps.h = img.getAttribute('height');
		}
		
	}else{
		if(ps.x){
			ps.x = img.offsetLeft;
		}
		if(ps.y){
			ps.y = img.offsetTop;	
		}
		if(ps.w){
			ps.w = img.offsetWidth;
		}
		if(ps.h){
			 ps.h = img.offsetHeight;
		}
		
	}
	return ps;
}
//�������ߵ�λ�á���ɫ����ϸ
function setLine(line,p){
	if(isFF){
		if(p.x1){
			line.setAttribute("x1",p.x1);
		}
		if(p.y1){
			line.setAttribute("y1",p.y1);
		}
		if(p.x2){
			line.setAttribute("x2",p.x2);
		}
		if(p.y2){
			line.setAttribute("y2",p.y2);
		}
		if(p.stroke){
			line.setAttribute("stroke",p.stroke);
		}
		if(p.strokeWidth){
			line.setAttribute("stroke-width",p.strokeWidth);
		}
		
	}else{
		if(p.x1&&p.y1){
			line.from = p.x1+','+p.y1;
		}
		
		if(p.x2&&p.y2){
			line.to = p.x2+','+p.y2;
		}
		if(p.stroke){
			line.strokecolor = p.stroke;
		}
		if(p.strokeWidth){
			line.strokeweight = p.strokeWidth;
		}
	}
}
//�����������label��������
function createElementByUserAgentAnd(label){
	var o;
	if(isFF){
		o = document.createElementNS("http://www.w3.org/2000/svg",label);
	}else{
		o = document.createElement("v:"+label);
	}
	return o;
}
//��������������ڵ��ǩ���ӵ�����
function appendChild(divText){
	if(isFF){
		document.body.appendChild(divText);
	}else{
		document.getElementById('divLayer').appendChild(divText);
	}
}
function removeChild(divText){
	if(isFF){
		document.body.removeChild(divText);
	}else{
		document.getElementById('divLayer').removeChild(divText);
	}
}

//��a,b�ڵ�õ������˵������
function getCoorObjectFrom(a,b){
	var x1,y1,x2,y2;
	if(isFF){
		if(a){
			x1 =(parseInt(a.getAttribute('x')) + parseInt(a.getAttribute('width'))/2 - 3),y1=(parseInt(a.getAttribute('y')) + parseInt(a.getAttribute('height'))/2 - 3);
				
		}
		if(b){
			x2 = (parseInt(b.getAttribute('x')) + parseInt(b.getAttribute('width'))/2 - 3),y2=(parseInt(b.getAttribute('y')) + parseInt(b.getAttribute('height'))/2 - 3);
				
		}
		
	}else{
		if(a){
			x1 =(parseInt(a.style.left) + parseInt(a.style.width)/2 - 3),y1=(parseInt(a.style.top) + parseInt(a.style.height)/2 - 3);
				
		}
		if(b){
			x2 = (parseInt(b.style.left) + parseInt(b.style.width)/2 - 3),y2=(parseInt(b.style.top) + parseInt(b.style.height)/2 - 3);
				
		}
		
	}
	return {'x1':x1,'y1':y1,'x2':x2,'y2':y2};
}

function cancelPropagationAndDefaultOfEvent(event){
	 if(event.stopPropagation){
	    event.stopPropagation();
	}else{
		event.cancelBubble = true;
	}
	event.preventDefault?event.preventDefault():(event.returnValue=false);
	
	return false; 
}

//IE10��oncontextmenu�¼� 
function addContextmenuEventListener(o,fn){
	if(isFF){//֧��w3c��׼
		o.addEventListener('contextmenu',fn,false);
	}else{
		o.attachEvent('oncontextmenu',fn);
	}
}

function serializeXmldocToString(){
	if(isFF){
		 return '<?xml version="1.0" encoding="GB2312"?>\n'+(new XMLSerializer()).serializeToString(xmldoc);
	}else{
		return xmldoc.xml;
	}
}

function discardSelectedLast(){
	if (objStyle != null)
	{
		unSelectImg(objStyle);
		objStyle = null;
	}
}