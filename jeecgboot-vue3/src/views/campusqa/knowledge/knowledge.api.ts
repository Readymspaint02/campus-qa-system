import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/campusqa/knowledge/list',
  edit = '/campusqa/knowledge/edit',
  get = '/campusqa/knowledge/queryById',
  delete = '/campusqa/knowledge/delete',
}

export const listKnowledge = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdateKnowledge = (params) => defHttp.post({ url: Api.edit, params });

export const getKnowledgeById = (params) => defHttp.get({ url: Api.get, params });

export const deleteKnowledge = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, data: params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
