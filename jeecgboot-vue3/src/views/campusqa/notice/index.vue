<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>
    <NoticeModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="campusqa-notice" setup>
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { columns, searchFormSchema } from './notice.data';
  import { listNotice, deleteNotice } from './notice.api';
  import NoticeModal from './NoticeModal.vue';

  const [registerModal, { openModal }] = useModal();

  const { tableContext } = useListPage({
    designScope: 'campusqa-notice',
    tableProps: {
      title: '通知公告',
      api: listNotice,
      columns,
      formConfig: {
        schemas: searchFormSchema,
      },
      actionColumn: {
        width: 200,
      },
    },
  });

  const [registerTable, { reload }] = tableContext;

  function getActions(record) {
    return [
      {
        label: '查看',
        onClick: handleView.bind(null, record),
      },
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '确认删除该记录吗？',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
  }

  function handleAdd() {
    openModal(true, {
      isUpdate: false,
    });
  }

  function handleView(record) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  function handleEdit(record) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  async function handleDelete(record) {
    await deleteNotice({ id: record.id }, reload);
  }
</script>
