package com.hiretaxi.receiver;

//public class MyPushMessageReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
//
//            Log.i("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
//
//            try {
//                Notifaction notifaction = new Notifaction();
//                JSONObject jsonObject = new JSONObject(intent.getStringExtra("msg"));
//                notifaction.setContent(jsonObject.getString("alert"));
////                Long _id = SystemClock.currentThreadTimeMillis();
//
//                Long _id = new Date().getTime();
//                notifaction.set_id(_id);
//                Log.i("bmob", "onReceive: isSaveOk===> " + 1);
//                //检查是否存在该条数据
//                boolean isHasThisData = checkNotifactionData(context, notifaction);
//                if (!isHasThisData) {
//                    //将数据保存在数据库
//                    boolean isSaveOk = getNotifactionUtilInstance(context).insertNotifaction(notifaction);
//                    Log.i("bmob", "onReceive: isSaveOk===> " + isSaveOk + "2");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private boolean checkNotifactionData(Context context, Notifaction notifaction) {
//        List<Notifaction> list = NotifactionUtil.getNotifactionUtilInstance(context).queryAllNotifaction();
//        if (list == null || list.isEmpty()) {
//            return false;
//        }
//        for (Notifaction notifactionIndex : list) {
//            if (null != notifactionIndex && notifactionIndex.getContent() != null
//                    && notifaction != null && notifaction.getContent() != null
//                    && notifactionIndex.getContent().equals(notifaction.getContent())) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
