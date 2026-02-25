import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/campusqa/category/list',
  edit = '/campusqa/category/edit',
  get = '/campusqa/category/queryById',
  delete = '/campusqa/category/delete',
}

export const listCategory = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdateCategory = (params) => defHttp.post({ url: Api.edit, params });

export const getCategoryById = (params) => defHttp.get({ url: Api.get, params });

export const deleteCategory = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
