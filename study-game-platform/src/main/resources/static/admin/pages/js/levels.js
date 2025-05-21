let levelsTable;
let subjects = [];
let chapters = {};

$(document).ready(function() {
    const baseUrl = window.location.protocol + '//' + window.location.host;
    const token = localStorage.getItem('adminToken');
    
    if (!token) {
        window.parent.location.href = '../login.html';
        return;
    }
    
    // 初始化DataTable
    levelsTable = $('#levelsTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.1/i18n/zh.json'
        },
        columns: [
            { data: 'id' },
            { data: 'name' },
            { 
                data: 'chapter',
                render: function(data) {
                    return data ? data.name : '';
                }
            },
            { data: 'sortOrder' },
            { 
                data: 'difficultyLevel',
                render: function(data) {
                    switch(parseInt(data)) {
                        case 1: return '<span class="badge bg-success">简单</span>';
                        case 2: return '<span class="badge bg-warning">中等</span>';
                        case 3: return '<span class="badge bg-danger">困难</span>';
                        default: return '<span class="badge bg-secondary">未知</span>';
                    }
                }
            },
            { data: 'totalQuestions' },
            { 
                data: 'status',
                render: function(data) {
                    return data === 1 ? 
                        '<span class="badge bg-success">启用</span>' : 
                        '<span class="badge bg-danger">禁用</span>';
                } 
            },
            { 
                data: 'createTime',
                render: function(data) {
                    console.log('Level date data:', data);
                    // 直接返回日期字符串，如果为空则显示短线
                    return data || '-';
                } 
            },
            {
                data: null,
                orderable: false,
                render: function(data) {
                    return `
                        <button class="btn btn-primary btn-circle edit-level" data-id="${data.id}" title="编辑">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-danger btn-circle delete-level" data-id="${data.id}" title="删除">
                            <i class="bi bi-trash"></i>
                        </button>
                        <button class="btn btn-info btn-circle questions-level" data-id="${data.id}" title="查看题目">
                            <i class="bi bi-question-circle"></i>
                        </button>
                    `;
                }
            }
        ]
    });
    
    // 加载所有学科
    loadSubjects();
    
    // 学科选择变化事件
    $('#subjectFilter, #levelSubject, #editLevelSubject').change(function() {
        const subjectId = $(this).val();
        const targetId = this.id;
        
        if (subjectId) {
            // 根据学科加载相应的章节
            if (targetId === 'subjectFilter') {
                loadChaptersForFilter(subjectId);
            } else if (targetId === 'levelSubject') {
                loadChaptersForAdd(subjectId);
            } else if (targetId === 'editLevelSubject') {
                loadChaptersForEdit(subjectId);
            }
        } else {
            // 清空章节选择
            if (targetId === 'subjectFilter') {
                $('#chapterFilter').html('<option value="">所有章节</option>');
                // 重新加载所有关卡
                loadLevels('', '');
            } else if (targetId === 'levelSubject') {
                $('#levelChapter').html('<option value="">请先选择学科</option>');
            } else if (targetId === 'editLevelSubject') {
                $('#editLevelChapter').html('<option value="">请先选择学科</option>');
            }
        }
    });
    
    // 章节筛选变化事件
    $('#chapterFilter').change(function() {
        const subjectId = $('#subjectFilter').val();
        const chapterId = $(this).val();
        
        loadLevels(subjectId, chapterId);
    });
    
    // 添加关卡
    $('#saveLevelBtn').click(function() {
        const chapterId = $('#levelChapter').val();
        const name = $('#levelName').val();
        const description = $('#levelDescription').val();
        const sortOrder = $('#levelOrder').val();
        const difficultyLevel = $('#levelDifficulty').val();
        const timeLimit = $('#levelTimeLimit').val();
        const status = $('#levelActive').is(':checked') ? 1 : 0;
        
        if (!chapterId) {
            alert('请选择章节');
            return;
        }
        
        if (!name) {
            alert('请填写关卡名称');
            return;
        }
        
        // 创建关卡对象
        const level = {
            chapterId: chapterId,
            name: name,
            description: description,
            sortOrder: sortOrder,
            difficultyLevel: difficultyLevel,
            timeLimit: timeLimit,
            status: status
        };
        
        fetch('/api/admin/levels', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(level)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('保存关卡失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                $('#addLevelModal').modal('hide');
                $('#addLevelForm')[0].reset();
                loadLevels($('#subjectFilter').val(), $('#chapterFilter').val());
                alert('关卡添加成功');
            } else {
                alert('添加失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('添加失败，请稍后重试');
        });
    });
    
    // 编辑关卡按钮点击事件
    $('#levelsTable').on('click', '.edit-level', function() {
        const id = $(this).data('id');
        const level = levelsTable.rows().data().toArray().find(l => l.id === id);
        
        if (level) {
            $('#editLevelId').val(level.id);
            $('#editLevelName').val(level.name);
            $('#editLevelDescription').val(level.description);
            $('#editLevelOrder').val(level.sortOrder);
            $('#editLevelDifficulty').val(level.difficultyLevel);
            $('#editLevelTimeLimit').val(level.timeLimit);
            $('#editLevelActive').prop('checked', level.status === 1);
            
            // 填充学科和章节选择框
            const chapterId = level.chapter?.id;
            const subjectId = level.chapter?.subject?.id;
            
            if (subjectId) {
                $('#editLevelSubject').val(subjectId);
                
                // 加载相应学科的章节
                loadChaptersForEdit(subjectId, chapterId);
            }
            
            $('#editLevelModal').modal('show');
        }
    });
    
    // 更新关卡
    $('#updateLevelBtn').click(function() {
        const id = $('#editLevelId').val();
        const chapterId = $('#editLevelChapter').val();
        const name = $('#editLevelName').val();
        const description = $('#editLevelDescription').val();
        const sortOrder = $('#editLevelOrder').val();
        const difficultyLevel = $('#editLevelDifficulty').val();
        const timeLimit = $('#editLevelTimeLimit').val();
        const status = $('#editLevelActive').is(':checked') ? 1 : 0;
        
        if (!chapterId) {
            alert('请选择章节');
            return;
        }
        
        if (!name) {
            alert('请填写关卡名称');
            return;
        }
        
        // 创建关卡对象
        const level = {
            id: id,
            chapterId: chapterId,
            name: name,
            description: description,
            sortOrder: sortOrder,
            difficultyLevel: difficultyLevel,
            timeLimit: timeLimit,
            status: status
        };
        
        fetch('/api/admin/levels/' + id, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(level)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('更新关卡失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                $('#editLevelModal').modal('hide');
                loadLevels($('#subjectFilter').val(), $('#chapterFilter').val());
                alert('关卡更新成功');
            } else {
                alert('更新失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('更新失败，请稍后重试');
        });
    });
    
    // 删除关卡按钮点击事件
    $('#levelsTable').on('click', '.delete-level', function() {
        const id = $(this).data('id');
        
        if (confirm('确定要删除此关卡吗？删除关卡将同时删除相关的题目数据。')) {
            fetch('/api/admin/levels/' + id, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('删除关卡失败');
                }
                return response.json();
            })
            .then(data => {
                if (data.code === 200) {
                    loadLevels($('#subjectFilter').val(), $('#chapterFilter').val());
                    alert('关卡删除成功');
                } else {
                    alert('删除失败：' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('删除失败，请稍后重试');
            });
        }
    });
    
    // 查看关卡题目按钮点击事件
    $('#levelsTable').on('click', '.questions-level', function() {
        const id = $(this).data('id');
        const level = levelsTable.rows().data().toArray().find(l => l.id === id);
        
        if (level) {
            // 跳转到题目管理页面并传递关卡ID
            window.parent.location.href = 'index.html?page=questions&levelId=' + id;
        }
    });
    
    // 加载所有学科
    function loadSubjects() {
        fetch('/api/admin/subjects', {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('获取学科数据失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                subjects = data.data || [];
                
                // 填充学科选择框
                populateSubjectSelects(subjects);
                
                // 加载关卡数据
                loadLevels('', '');
            } else {
                console.error('Failed to load subjects:', data.message);
                alert('加载学科数据失败: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error loading subjects:', error);
            alert('加载学科数据失败，请稍后重试');
            
            // 如果加载失败，仍然显示空表格
            populateSubjectSelects([]);
            levelsTable.clear().draw();
        });
    }
    
    // 填充学科选择框
    function populateSubjectSelects(subjects) {
        const $subjectFilter = $('#subjectFilter');
        const $levelSubject = $('#levelSubject');
        const $editLevelSubject = $('#editLevelSubject');
        
        $subjectFilter.html('<option value="">所有学科</option>');
        $levelSubject.html('<option value="">请选择学科</option>');
        $editLevelSubject.html('<option value="">请选择学科</option>');
        
        subjects.forEach(subject => {
            if (subject.status === 1) {
                $subjectFilter.append(`<option value="${subject.id}">${subject.name}</option>`);
                $levelSubject.append(`<option value="${subject.id}">${subject.name}</option>`);
                $editLevelSubject.append(`<option value="${subject.id}">${subject.name}</option>`);
            }
        });
    }
    
    // 加载筛选器章节选择框
    function loadChaptersForFilter(subjectId) {
        fetch(`/api/admin/chapters/subject/${subjectId}`, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('获取章节数据失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                chapters[subjectId] = data.data || [];
                
                // 更新章节选择框
                const $chapterFilter = $('#chapterFilter');
                $chapterFilter.html('<option value="">所有章节</option>');
                
                chapters[subjectId].forEach(chapter => {
                    if (chapter.status === 1) {
                        $chapterFilter.append(`<option value="${chapter.id}">${chapter.name}</option>`);
                    }
                });
                
                // 重新加载关卡
                loadLevels(subjectId, '');
            } else {
                console.error('Failed to load chapters:', data.message);
                alert('加载章节数据失败: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error loading chapters:', error);
            alert('加载章节数据失败，请稍后重试');
            
            // 如果加载失败，清空章节选择框
            $('#chapterFilter').html('<option value="">所有章节</option>');
            
            // 仍然尝试加载关卡
            loadLevels(subjectId, '');
        });
    }
    
    // 加载添加表单章节选择框
    function loadChaptersForAdd(subjectId) {
        fetch(`/api/admin/chapters/subject/${subjectId}`, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('获取章节数据失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                chapters[subjectId] = data.data || [];
                
                // 更新章节选择框
                const $levelChapter = $('#levelChapter');
                $levelChapter.html('<option value="">请选择章节</option>');
                
                chapters[subjectId].forEach(chapter => {
                    if (chapter.status === 1) {
                        $levelChapter.append(`<option value="${chapter.id}">${chapter.name}</option>`);
                    }
                });
            } else {
                console.error('Failed to load chapters:', data.message);
                alert('加载章节数据失败: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error loading chapters:', error);
            alert('加载章节数据失败，请稍后重试');
            
            // 如果加载失败，清空章节选择框
            $('#levelChapter').html('<option value="">请选择章节</option>');
        });
    }
    
    // 加载编辑表单章节选择框
    function loadChaptersForEdit(subjectId, selectedChapterId) {
        fetch(`/api/admin/chapters/subject/${subjectId}`, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('获取章节数据失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                chapters[subjectId] = data.data || [];
                
                // 更新章节选择框
                const $editLevelChapter = $('#editLevelChapter');
                $editLevelChapter.html('<option value="">请选择章节</option>');
                
                chapters[subjectId].forEach(chapter => {
                    if (chapter.status === 1) {
                        $editLevelChapter.append(`<option value="${chapter.id}">${chapter.name}</option>`);
                    }
                });
                
                // 如果有选中的章节，设置选中状态
                if (selectedChapterId) {
                    $editLevelChapter.val(selectedChapterId);
                }
            } else {
                console.error('Failed to load chapters:', data.message);
                alert('加载章节数据失败: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error loading chapters:', error);
            alert('加载章节数据失败，请稍后重试');
            
            // 如果加载失败，清空章节选择框
            $('#editLevelChapter').html('<option value="">请选择章节</option>');
        });
    }
    
    // 加载关卡数据
    function loadLevels(subjectId, chapterId) {
        let url = '/api/admin/levels';
        
        // 构建查询 URL
        if (chapterId) {
            url += '/chapter/' + chapterId;
        } else if (subjectId) {
            url += '/subject/' + subjectId;
        }
        
        fetch(url, {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('获取关卡数据失败');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                let levels = data.data || [];
                
                if (levels.length === 0) {
                    // 显示空表格，没有数据
                    levelsTable.clear().draw();
                    console.log('No levels found');
                } else {
                    levelsTable.clear();
                    levelsTable.rows.add(levels).draw();
                }
            } else {
                console.error('Failed to load levels:', data.message);
                alert('加载关卡数据失败: ' + data.message);
                levelsTable.clear().draw();
            }
        })
        .catch(error => {
            console.error('Error loading levels:', error);
            alert('加载关卡数据失败，请稍后重试');
            levelsTable.clear().draw();
        });
    }
});
