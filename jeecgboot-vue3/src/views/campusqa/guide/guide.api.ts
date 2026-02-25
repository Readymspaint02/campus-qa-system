import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/campusqa/guide/list',
  edit = '/campusqa/guide/edit',
  get = '/campusqa/guide/queryById',
  delete = '/campusqa/guide/delete',
}

export const listGuide = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdateGuide = (params) => defHttp.post({ url: Api.edit, params });

export const getGuideById = (params) => defHttp.get({ url: Api.get, params });

export const deleteGuide = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
