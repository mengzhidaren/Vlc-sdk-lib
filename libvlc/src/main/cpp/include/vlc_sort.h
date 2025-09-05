/******************************************************************************
 * vlc_sort.h
 ******************************************************************************
 * Copyright © 2019 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

#ifndef VLC_SORT_H
#define VLC_SORT_H

#include <stdlib.h>
#include <stddef.h>

/**
 * Sort an array with reentrancy, following the upcoming POSIX prototype
 *
 * cf. POSIX qsort_r
 */
VLC_API void vlc_qsort(void *base, size_t nmemb, size_t size,
                       int (*compar)(const void *, const void *, void *),
                       void *arg);

#endif
