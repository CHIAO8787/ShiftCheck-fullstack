const API="/api/handover-items", $=s=>document.querySelector(s);
const statusLabels={TODO:"未対応",DOING:"対応中",DONE:"完了"}, priorityLabels={HIGH:"重要度：高",MEDIUM:"重要度：中",LOW:"重要度：低"};
let tasks=[];
async function api(path="",opts={}) {
 const r=await fetch(API+path,{...opts,headers:{"Content-Type":"application/json",...(opts.headers||{})}});
 if(r.status===204)return null; const b=await r.json().catch(()=>({}));
 if(!r.ok)throw new Error(b.message||`API error (${r.status})`); return b;
}
function toast(msg,error=false){document.querySelector(".toast")?.remove();const n=document.createElement("div");n.className="toast"+(error?" error":"");n.textContent=msg;document.body.append(n);setTimeout(()=>n.remove(),3200)}
function date(v){if(!v)return"期限未設定";const d=new Date(v);return Number.isNaN(+d)?"期限未設定":new Intl.DateTimeFormat("ja-JP",{month:"numeric",day:"numeric",hour:"2-digit",minute:"2-digit"}).format(d)}
function tag(text,extra=""){const e=document.createElement("span");e.className="tag "+extra;e.textContent=text;return e}
function render(){
 const q=$("#searchInput").value.trim().toLocaleLowerCase("ja-JP"),s=$("#statusFilter").value,p=$("#priorityFilter").value;
 const shown=tasks.filter(t=>(!q||[t.title,t.assignee||"",t.category||""].join(" ").toLocaleLowerCase("ja-JP").includes(q))&&(!s||t.status===s)&&(!p||t.priority===p)).sort((a,b)=>Number(b.overdue)-Number(a.overdue));
 const list=$("#taskList");list.replaceChildren();$("#emptyState").classList.toggle("hidden",shown.length>0);
 for(const t of shown){
  const card=document.createElement("article");card.className="task-card";const main=document.createElement("div"),tags=document.createElement("div");tags.className="tags";
  tags.append(tag(t.category||"申し送り"),tag(priorityLabels[t.priority]||"重要度：中",t.priority.toLowerCase()));if(t.overdue)tags.append(tag("期限超過","overdue"));
  const title=document.createElement("h3");title.className="task-title";title.textContent=t.title;
  const meta=document.createElement("div");meta.className="meta";const a=document.createElement("span");a.textContent=`担当：${t.assignee||"未設定"}`;const due=document.createElement("span");due.textContent=`期限：${date(t.dueAt)}`;meta.append(a,due);main.append(tags,title,meta);
  const controls=document.createElement("div");controls.className="controls";const row=document.createElement("label");row.className="status-row";row.append(document.createTextNode("対応状況"));const select=document.createElement("select");select.setAttribute("aria-label",`${t.title} の対応状況`);
  Object.entries(statusLabels).forEach(([v,l])=>{const o=document.createElement("option");o.value=v;o.textContent=l;select.append(o)});select.value=t.status;
  select.addEventListener("change",async()=>{try{await api("/"+t.id,{method:"PATCH",body:JSON.stringify({status:select.value})});await load();toast("対応状況を更新しました")}catch(e){toast(e.message,true);await load()}});row.append(select);
  const cLabel=document.createElement("label");cLabel.className="confirm";const cb=document.createElement("input");cb.type="checkbox";cb.checked=t.confirmed;cb.setAttribute("aria-label",`${t.title} の引き継ぎ確認`);
  cb.addEventListener("change",async()=>{try{await api("/"+t.id,{method:"PATCH",body:JSON.stringify({confirmed:cb.checked})});await load();toast("引き継ぎ確認を更新しました")}catch(e){toast(e.message,true);await load()}});
  cLabel.append(cb,document.createTextNode("引き継ぎ確認済み"));const del=document.createElement("button");del.className="delete";del.textContent="削除";del.type="button";
  del.addEventListener("click",async()=>{if(!confirm(`「${t.title}」を削除しますか？`))return;try{await api("/"+t.id,{method:"DELETE"});await load();toast("削除しました")}catch(e){toast(e.message,true)}});
  controls.append(row,cLabel,del);card.append(main,controls);list.append(card);
 }
 $("#totalCount").textContent=tasks.length;$("#openCount").textContent=tasks.filter(t=>t.status!=="DONE").length;$("#overdueCount").textContent=tasks.filter(t=>t.overdue).length;$("#confirmedCount").textContent=tasks.filter(t=>t.confirmed).length;$("#resultCount").textContent=`${shown.length}件を表示`;
 const attention=tasks.filter(t=>t.overdue||!t.confirmed).sort((a,b)=>Number(b.overdue)-Number(a.overdue)).slice(0,5),box=$("#attentionList");box.replaceChildren();
 if(!attention.length){const e=document.createElement("div");e.className="attention-empty";e.textContent="確認が必要な項目はありません。";box.append(e)}
 attention.forEach(t=>{const e=document.createElement("div");e.className="attention";const h=document.createElement("strong"),m=document.createElement("span");h.textContent=t.title;m.textContent=`${t.overdue?"期限超過":"引き継ぎ未確認"} · ${statusLabels[t.status]}`;e.append(h,m);box.append(e)});
}
async function load(){
 try{const p=new URLSearchParams();if($("#searchInput").value.trim())p.set("q",$("#searchInput").value.trim());if($("#statusFilter").value)p.set("status",$("#statusFilter").value);if($("#priorityFilter").value)p.set("priority",$("#priorityFilter").value);
 tasks=await api("?"+p.toString());$("#apiState").textContent="API 接続中";$("#apiState").classList.remove("error");render();
 }catch(e){$("#apiState").textContent="API 接続エラー";$("#apiState").classList.add("error");$("#taskList").replaceChildren();$("#emptyState").classList.remove("hidden");$("#emptyState").querySelector("h3").textContent="サーバーに接続できません";$("#emptyState").querySelector("p").textContent="PostgreSQL と Spring Boot が起動しているか確認してください。";toast(e.message,true)}
}
function openDialog(){ $("#taskForm").reset();const d=new Date(Date.now()+86400000);d.setMinutes(d.getMinutes()-d.getTimezoneOffset());$("#dueInput").value=d.toISOString().slice(0,16);$("#taskDialog").showModal();$("#titleInput").focus()}
$("#addButton").addEventListener("click",openDialog);$("#closeDialog").addEventListener("click",()=>$("#taskDialog").close());$("#cancelDialog").addEventListener("click",()=>$("#taskDialog").close());
$("#taskForm").addEventListener("submit",async e=>{e.preventDefault();const title=$("#titleInput").value.trim();if(!title){$("#titleInput").focus();return}const due=$("#dueInput").value;
 try{await api("",{method:"POST",body:JSON.stringify({title,assignee:$("#assigneeInput").value.trim()||null,category:$("#categoryInput").value,dueAt:due||null,priority:$("#priorityInput").value})});$("#taskDialog").close();await load();toast("申し送りを追加しました")}catch(err){toast(err.message,true)}});
let timer;$("#searchInput").addEventListener("input",()=>{clearTimeout(timer);timer=setTimeout(load,180)});$("#statusFilter").addEventListener("change",load);$("#priorityFilter").addEventListener("change",load);
$("#todayLabel").textContent=new Intl.DateTimeFormat("ja-JP",{year:"numeric",month:"long",day:"numeric",weekday:"short"}).format(new Date());load();
