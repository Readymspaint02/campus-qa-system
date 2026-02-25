import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/campusqa/notice/list',
  edit = '/campusqa/notice/edit',
  get = '/campusqa/notice/queryById',
  delete = '/campusqa/notice/delete',
}

export const listNotice = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdateNotice = (params) => defHttp.post({ url: Api.edit, params });

export const getNoticeById = (params) => defHttp.get({ url: Api.get, params });

export const deleteNotice = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
