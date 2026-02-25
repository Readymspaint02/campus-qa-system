import { BasicColumn, FormSchema } from '/@/components/Table';

import { render } from '/@/utils/common/renderUtils';

const statusOptions = [
  { label: '启用', value: 'enable' },
  { label: '停用', value: 'disable' },
];

const categoryDictCode = 'campus_qa_category,name,id';

const statusMap: Record<string, string> = {
  enable: '启用',
  disable: '停用',
};

export const columns: BasicColumn[] = [
  {
    title: '标题',
    dataIndex: 'title',
    align: 'left',
  },
  {
    title: '分类',
    dataIndex: 'categoryId',
    width: 160,
    customRender: ({ text }) => render.renderDict(text, categoryDictCode),
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }) => statusMap[String(text)] || text,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 180,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'title',
    label: '标题',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'categoryId',
    label: '分类',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: categoryDictCode,
      placeholder: '请选择分类',
    },
    colProps: { span: 8 },
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
    colProps: { span: 8 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '编号',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'categoryId',
    label: '分类',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: categoryDictCode,
      placeholder: '请选择分类',
    },
    required: true,
  },
  {
    field: 'title',
    label: '标题',
    component: 'Input',
    required: true,
  },
  {
    field: 'content',
    label: '内容',
    component: 'InputTextArea',
    required: true,
  },
  {
    field: 'steps',
    label: '办理步骤',
    component: 'InputTextArea',
  },
  {
    field: 'contact',
    label: '联系方式',
    component: 'Input',
  },
  {
    field: 'location',
    label: '办理地点',
    component: 'Input',
  },
  {
    field: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
  },
];
