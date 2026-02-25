import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/campusqa/feedback/list',
  edit = '/campusqa/feedback/edit',
  get = '/campusqa/feedback/queryById',
  delete = '/campusqa/feedback/delete',
}

export const listFeedback = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdateFeedback = (params) => defHttp.post({ url: Api.edit, params });

export const getFeedbackById = (params) => defHttp.get({ url: Api.get, params });

export const deleteFeedback = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
